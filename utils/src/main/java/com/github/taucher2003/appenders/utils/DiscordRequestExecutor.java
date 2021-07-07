/*
 *
 *  Copyright 2020 Niklas van Schrick and the Appenders Project
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class DiscordRequestExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiscordRequestExecutor.class);

    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private final Marker selfIgnoringMarker;
    private final DiscordRequester.Bucket bucket;
    private final OkHttpClient httpClient;

    private final BlockingQueue<DataPair<Request, CompletableFuture<Void>>> requests = new LinkedBlockingQueue<>();
    private final AtomicReference<ScheduledFuture<?>> currentQueueExecution = new AtomicReference<>();
    private boolean shuttingDown;

    public DiscordRequestExecutor(Marker selfIgnoringMarker, OkHttpClient httpClient) {
        this.selfIgnoringMarker = selfIgnoringMarker;
        this.bucket = new DiscordRequester.Bucket(selfIgnoringMarker);
        this.httpClient = httpClient;
    }

    boolean addToQueue(DataPair<Request, CompletableFuture<Void>> request) {
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
            DataPair<Request, CompletableFuture<Void>> request = requests.poll();
            if (bucket.isRatelimit()) {
                delayQueue();
                break;
            }
            boolean successful = executePair(request);
            if (!successful) {
                break;
            }
        }
        currentQueueExecution.set(null);
        if (shuttingDown && requests.isEmpty()) {
            executorService.shutdown();
        }
    }

    private boolean executePair(DataPair<Request, ? extends CompletableFuture<Void>> request) {
        try (Response response = httpClient.newCall(request.getFirst()).execute()) {
            bucket.update(response);
            if (response.code() == 429) {
                delayQueue();
                return false;
            }
            if (!response.isSuccessful()) {
                LOGGER.error(selfIgnoringMarker, "Sending a webhook message failed with non-OK http response ({})", response.code());
                IOException exception = new IOException("Sending Bot message has failed with HTTP code " + response.code());
                exception.fillInStackTrace();
                request.getSecond().completeExceptionally(exception);
                return true;
            }
            request.getSecond().complete(null);
            if (bucket.isRatelimit()) {
                delayQueue();
                return false;
            }
        } catch (IOException e) {
            LOGGER.error(selfIgnoringMarker, "There was some error while sending a webhook message", e);
            request.getSecond().completeExceptionally(e);
        }
        return true;
    }

    public void shutdown() {
        shuttingDown = true;
    }
}
