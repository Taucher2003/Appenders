/*
 *
 *  Copyright 2021 Niklas van Schrick and the contributors of the Appenders Project
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

import okhttp3.Request;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.RejectedExecutionException;

/**
 * Wrapper class which controls the underlying {@link WebRequestExecutor}
 */
public class WebRequester {

    private final WebRequestExecutor executor;

    /**
     * Creates a new WebRequester
     *
     * @param executor the underlying {@link WebRequestExecutor} which should be managed
     */
    public WebRequester(WebRequestExecutor executor) {
        this.executor = executor;
    }

    /**
     * Adds a request to the underlying {@link WebRequestExecutor}
     *
     * @param request the request which should be queued
     * @return a {@link CompletableFuture} which will complete with the response body as string
     */
    public CompletableFuture<String> request(Request request) {
        CompletableFuture<String> future = new CompletableFuture<>();
        DataPair<Request, CompletableFuture<String>> pair = new DataPair<>(request, future);
        if (executor.addToQueue(pair)) {
            return future;
        }
        future.completeExceptionally(new RejectedExecutionException("Request has not been added to queue"));
        return future;
    }

    /**
     * Shuts down the underlying {@link WebRequestExecutor}
     */
    public void shutdown() {
        executor.shutdown();
    }
}
