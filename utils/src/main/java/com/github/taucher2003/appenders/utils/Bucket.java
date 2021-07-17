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

import okhttp3.Response;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public abstract class Bucket {
    protected final AtomicInteger remaining = new AtomicInteger();
    protected final AtomicLong resetAt = new AtomicLong(System.currentTimeMillis());
    protected final AtomicLong retryAfter = new AtomicLong();

    public boolean isRatelimit() {
        return remaining.get() == 0 && System.currentTimeMillis() <= resetAt.get();
    }

    protected abstract void handleRatelimit(Response response) throws IOException;

    protected abstract void update(Response response) throws IOException;

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
