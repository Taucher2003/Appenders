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

import okhttp3.Response;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Abstract base class for handling ratelimit buckets
 */
public abstract class Bucket {
    protected final AtomicInteger remaining = new AtomicInteger();
    protected final AtomicLong resetAt = new AtomicLong(System.currentTimeMillis());
    protected final AtomicLong retryAfter = new AtomicLong();

    /**
     * Checks if this bucket is currently ratelimited
     *
     * @return a boolean indicating the ratelimit status
     */
    public boolean isRatelimit() {
        return remaining.get() == 0 && System.currentTimeMillis() <= resetAt.get();
    }

    /**
     * Handles a ratelimited response and updates this bucket with the current data
     *
     * @param response the ratelimited response
     * @throws IOException if reading of the response body fails
     */
    protected abstract void handleRatelimit(Response response) throws IOException;

    /**
     * Reads the current ratelimit data from the response and updates this bucket with its data
     *
     * @param response the response of the executed request
     * @throws IOException if reading of the response body fails
     */
    protected abstract void update(Response response) throws IOException;

    /**
     * Returns the amount of remaining uses within the ratelimit period
     *
     * @return an integer indicating the amount
     */
    public int getRemaining() {
        return remaining.get();
    }

    /**
     * Returns the timestamp when the ratelimit resets and the remaining uses are reset to the route maximum
     *
     * @return a long holding the timestamp
     */
    public long getResetAt() {
        return resetAt.get();
    }

    /**
     * After encountering a ratelimited response this value is set to the amount of milliseconds after which the request may be retried
     *
     * @return a long holding the amount of milliseconds
     */
    public long getRetryAfter() {
        return retryAfter.get();
    }
}
