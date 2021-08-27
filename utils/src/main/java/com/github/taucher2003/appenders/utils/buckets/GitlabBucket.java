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

import com.github.taucher2003.appenders.utils.Bucket;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

import java.io.IOException;

/**
 * Implementation class of the {@link Bucket} for GitLab requests
 */
public class GitlabBucket extends Bucket {

    private static final Logger LOGGER = LoggerFactory.getLogger(GithubBucket.class);

    private final Marker selfIgnoringMarker;

    /**
     * Creates an instance of the GitlabBucket for handling the ratelimit
     *
     * @param selfIgnoringMarker the marker used by the calling appender to ignore log messages
     */
    public GitlabBucket(Marker selfIgnoringMarker) {
        this.selfIgnoringMarker = selfIgnoringMarker;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void handleRatelimit(Response response) throws IOException {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void update(Response response) throws IOException {

    }

    @Override
    public boolean isRatelimit() {
        return false;
    }

    @Override
    public int getRemaining() {
        return 1;
    }

    @Override
    public long getResetAt() {
        return System.currentTimeMillis();
    }

    @Override
    public long getRetryAfter() {
        return 0;
    }
}
