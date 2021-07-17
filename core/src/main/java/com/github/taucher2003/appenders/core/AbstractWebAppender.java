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

package com.github.taucher2003.appenders.core;

import com.github.taucher2003.appenders.utils.DataPair;
import com.github.taucher2003.appenders.utils.WebRequestExecutor;
import com.github.taucher2003.appenders.utils.WebRequester;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public abstract class AbstractWebAppender extends AbstractAppender {

    public static final MediaType APPLICATION_JSON = MediaType.parse("application/json");

    protected final WebRequester requester;
    protected static final OkHttpClient HTTP_CLIENT = new OkHttpClient();
    protected String baseUrl;

    protected AbstractWebAppender(WebRequestExecutor executor) {
        this.requester = new WebRequester(executor);
    }

    @Override
    protected void doAppend(LogEntry logEntry) {
        Request request = createRequest(logEntry);
        add(request);
    }

    protected void add(Request request) {
        try {
            DataPair<Integer, TimeUnit> maxExecutionTime = maxExecutionTime();
            requester.request(request).get(maxExecutionTime.getFirst(), maxExecutionTime.getSecond());
        } catch (InterruptedException | ExecutionException | TimeoutException exception) {
            LOGGER.error(SELF_IGNORE_MARKER, "Failed to execute web request", exception);
        }
    }

    @Override
    protected void flush() {
        // Do nothing, all requests are going to be executed directly
        // This behavior can be overridden in subclasses
    }

    protected DataPair<Integer, TimeUnit> maxExecutionTime() {
        return new DataPair<>(1, TimeUnit.HOURS);
    }

    protected abstract Request createRequest(LogEntry logEntry);

    // ---- Setters

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
