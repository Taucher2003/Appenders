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

package com.github.taucher2003.appenders.utils.executors;

import com.github.taucher2003.appenders.utils.Bucket;
import com.github.taucher2003.appenders.utils.WebRequestExecutor;
import com.github.taucher2003.appenders.utils.buckets.DiscordBucket;
import okhttp3.OkHttpClient;
import org.slf4j.Marker;

public class DiscordWebRequestExecutor extends WebRequestExecutor {
    public DiscordWebRequestExecutor(Marker selfIgnoringMarker, OkHttpClient httpClient) {
        super(selfIgnoringMarker, httpClient);
    }

    @Override
    protected Bucket createBucket(Marker selfIgnoringMarker) {
        return new DiscordBucket(selfIgnoringMarker);
    }
}
