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

package com.github.taucher2003.appenders.core.github;

import com.github.taucher2003.appenders.core.AbstractWebAppender;
import com.github.taucher2003.appenders.core.LogEntry;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class CommentingIssueAppender extends IssueAppender {

    @Override
    protected Request createRequest(LogEntry logEntry) {
        CompletableFuture<String> future = requester.request(createIssuesRequest());
        try {
            String response = future.get(10, TimeUnit.SECONDS);
            Iterable<Object> array = new JSONArray(response);
            for (Object object : array) {
                if (object instanceof JSONObject) {
                    JSONObject json = (JSONObject) object;

                    if (json.getString("title").equals(logEntry.getThrowable().toString())) {
                        return createIssueCommentRequest(json.getInt("number"), logEntry);
                    }
                }
            }
        } catch (InterruptedException | ExecutionException | TimeoutException exception) {
            LOGGER.warn(SELF_IGNORE_MARKER, "Failed to load github issues, creating a new one", exception);
        }

        return super.createRequest(logEntry);
    }

    private Request createIssuesRequest() {
        return createBaseRequest("/issues").get().build();
    }

    private Request createIssueCommentRequest(int issueId, LogEntry logEntry) {
        return createBaseRequest("/issues/" + issueId + "/comments").post(createIssueCommentRequestBody(logEntry)).build();
    }

    private RequestBody createIssueCommentRequestBody(LogEntry logEntry) {
        JSONObject json = new JSONObject().put("body", createBody(logEntry));
        return RequestBody.create(AbstractWebAppender.APPLICATION_JSON, json.toString());
    }
}
