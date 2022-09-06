/*
 *
 *  Copyright 2022 Niklas van Schrick and the contributors of the Appenders Project
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.github.taucher2003.appenders.utils;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Abstract base class for web request execution
 */
public abstract class WebRequestExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebRequestExecutor.class);

    private final ScheduledExecutorService executorService = Utilities.getExpiringScheduledExecutorService();
    private final Marker selfIgnoringMarker;
    private final Bucket bucket;
    private final OkHttpClient httpClient;

    private final BlockingQueue<DataPair<Request, CompletableFuture<String>>> requests = new LinkedBlockingQueue<>();
    private final AtomicReference<ScheduledFuture<?>> currentQueueExecution = new AtomicReference<>();
    private boolean shuttingDown;

    /**
     * Creates a new instance of a web request executor
     *
     * @param selfIgnoringMarker the marker used by the calling appender to ignore log messages
     * @param httpClient         the http client to use
     */
    public WebRequestExecutor(Marker selfIgnoringMarker, OkHttpClient httpClient) {
        this.selfIgnoringMarker = selfIgnoringMarker;
        this.bucket = createBucket(selfIgnoringMarker);
        this.httpClient = httpClient;
    }

    /**
     * Creates the correct {@link Bucket} instance for the executor
     *
     * @param selfIgnoringMarker the marker used by the calling appender to ignore log messages
     * @return the created Bucket
     */
    protected abstract Bucket createBucket(Marker selfIgnoringMarker);

    /**
     * Adds a request to the execution queue.
     *
     * @param request a {@link DataPair} which contains the {@link Request} and a {@link CompletableFuture}
     * @return a boolean indicating if the request has been added to execution queue
     */
    boolean addToQueue(DataPair<Request, CompletableFuture<String>> request) {
        boolean added = requests.add(request);
        if (added && currentQueueExecution.get() == null) {
            delayQueue();
        }
        return added;
    }

    private void delayQueue() {
        long retryAfter = bucket.getRetryAfter();
        long resetIn = bucket.getResetAt() - System.currentTimeMillis();
        if (retryAfter > 0 || resetIn > 0) {
            LOGGER.debug(selfIgnoringMarker, "Delaying queue execution for {}", Math.max(retryAfter, resetIn));
        }
        currentQueueExecution.set(executorService.schedule(this::executeQueue, Math.max(retryAfter, resetIn), TimeUnit.MILLISECONDS));
    }

    private synchronized void executeQueue() {
        while (!requests.isEmpty()) {
            if (bucket.isRatelimit()) {
                delayQueue();
                break;
            }
            DataPair<Request, CompletableFuture<String>> request = requests.peek();
            boolean successful = executePair(request);
            if (!successful) {
                break;
            }
            requests.poll();
        }
        currentQueueExecution.set(null);
        if (shuttingDown && requests.isEmpty()) {
            executorService.shutdown();
        }
    }

    private boolean executePair(DataPair<Request, ? extends CompletableFuture<String>> request) {
        try (Response response = httpClient.newCall(request.getFirst()).execute()) {
            bucket.update(response);
            if (response.code() == 429) {
                delayQueue();
                return false;
            }
            if (!response.isSuccessful()) {
                LOGGER.error(selfIgnoringMarker, "Sending a web request failed with non-OK http response ({})", response.code());
                IOException exception = new IOException("Sending web request has failed with HTTP code " + response.code());
                exception.fillInStackTrace();
                request.getSecond().completeExceptionally(exception);
                return true;
            }
            ResponseBody body = response.body();
            if (body != null) {
                request.getSecond().complete(body.string());
            } else {
                request.getSecond().complete(null);
            }
            if (bucket.isRatelimit()) {
                delayQueue();
                return false;
            }
        } catch (IOException e) {
            LOGGER.error(selfIgnoringMarker, "There was some error while sending a web request", e);
            request.getSecond().completeExceptionally(e);
        }
        return true;
    }

    /**
     * Marks this executor for shutdown
     * The shutdown takes part after all requests have been processed
     */
    public void shutdown() {
        shuttingDown = true;
    }
}
