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

import club.minnced.discord.webhook.IOUtil;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class DiscordRequester {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiscordRequester.class);

    private final DiscordRequestExecutor executor;

    public DiscordRequester(Marker selfIgnoringMarker, OkHttpClient httpClient) {
        this.executor = new DiscordRequestExecutor(selfIgnoringMarker, httpClient);
    }

    public CompletableFuture<Void> request(Request request) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        DataPair<Request, CompletableFuture<Void>> pair = new DataPair<>(request, future);
        if (!executor.addToQueue(pair)) {
            future.completeExceptionally(new RejectedExecutionException("Request has not been added to queue"));
        }
        return future;
    }

    public void shutdown() {
        executor.shutdown();
    }

    public static class Bucket {
        private final Marker selfIgnoringMarker;

        private final AtomicInteger remaining = new AtomicInteger();
        private final AtomicLong resetAt = new AtomicLong(System.currentTimeMillis());
        private final AtomicLong retryAfter = new AtomicLong();

        public Bucket(Marker selfIgnoringMarker) {
            this.selfIgnoringMarker = selfIgnoringMarker;
        }

        public boolean isRatelimit() {
            return remaining.get() == 0 && System.currentTimeMillis() <= resetAt.get();
        }

        private void handleRatelimit(Response response) throws IOException {
            String retryAfter = response.header("Retry-After");
            long retryAfterDelay;
            if (retryAfter == null) {
                InputStream stream = IOUtil.getBody(response);
                if (stream == null) {
                    retryAfterDelay = 30000;
                } else {
                    JSONObject body = IOUtil.toJSON(stream);
                    retryAfterDelay = (long) Math.ceil(body.getDouble("retry_after")) * 1000;
                }
            } else {
                retryAfterDelay = Long.parseLong(retryAfter) * 1000;
            }
            LOGGER.warn(selfIgnoringMarker, "Encountered 429, retrying after {} ms", retryAfterDelay);
            remaining.set(0);
            this.retryAfter.set(retryAfterDelay);
            this.resetAt.set(System.currentTimeMillis() + retryAfterDelay);
        }

        protected void update(Response response) throws IOException {
            boolean isRatelimited = response.code() == 429;
            String remaining = response.header("X-RateLimit-Remaining");
            String limit = response.header("X-RateLimit-Limit");
            String resetAfter = response.header("X-RateLimit-Reset-After");
            if (isRatelimited) {
                handleRatelimit(response);
                return;
            }
            if (remaining == null || limit == null || resetAfter == null) {
                LOGGER.debug(selfIgnoringMarker, "Failed to update buckets due to missing headers in response with code: {} and headers: \n{}",
                        response.code(), response.headers());
                return;
            }
            this.remaining.set(Integer.parseInt(remaining));

            long reset = (long) Math.ceil(Double.parseDouble(resetAfter));
            long delay = reset * 1000;
            this.resetAt.set(System.currentTimeMillis() + delay);
        }

        public int getRemaining() {
            return remaining.get();
        }

        public long getResetAt() {
            return resetAt.get();
        }

        public long getRetryAfter() {
            return retryAfter.get();
        }
    }
}
