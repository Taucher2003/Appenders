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

package com.github.taucher2003.appenders.utils.buckets;

import club.minnced.discord.webhook.IOUtil;
import com.github.taucher2003.appenders.utils.Bucket;
import okhttp3.Response;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

import java.io.IOException;
import java.io.InputStream;

/**
 * Implementation class of the {@link Bucket} for Discord requests
 */
public class DiscordBucket extends Bucket {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiscordBucket.class);

    private final Marker selfIgnoringMarker;

    /**
     * Creates an instance of the DiscordBucket for handling the ratelimit
     */
    public DiscordBucket(Marker selfIgnoringMarker) {
        this.selfIgnoringMarker = selfIgnoringMarker;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void handleRatelimit(Response response) throws IOException {
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

    /**
     * {@inheritDoc}
     */
    @Override
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
}
