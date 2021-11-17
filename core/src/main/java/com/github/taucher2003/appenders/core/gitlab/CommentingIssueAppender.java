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

package com.github.taucher2003.appenders.core.gitlab;

import com.github.taucher2003.appenders.core.AbstractWebAppender;
import com.github.taucher2003.appenders.core.LogEntry;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class CommentingIssueAppender extends IssueAppender {

    @Override
    protected Request createRequest(LogEntry logEntry) {
        CompletableFuture<String> future = requester.request(createIssuesRequest(logEntry.getThrowable().toString()));
        try {
            String response = future.get(30, TimeUnit.SECONDS);
            Iterable<Object> array = new JSONArray(response);
            for (Object object : array) {
                if (object instanceof JSONObject) {
                    JSONObject json = (JSONObject) object;
                    return createIssueCommentRequest(json.getInt("iid"), logEntry, json.getBoolean("confidential"));
                }
            }
        } catch (InterruptedException | ExecutionException | TimeoutException exception) {
            LOGGER.warn(SELF_IGNORE_MARKER, "Failed to load gitlab issues, creating a new one", exception);
        }

        return super.createRequest(logEntry);
    }

    private Request createIssuesRequest(String search) {
        try {
            return createBaseRequest("/issues?state=opened&search=" + URLEncoder.encode(search, "UTF-8")).get().build();
        } catch (UnsupportedEncodingException e) {
            // should never happen
            LOGGER.error(SELF_IGNORE_MARKER, "Failed to load issues due to unknown encoding", e);
        }
        return null;
    }

    private Request createIssueCommentRequest(int issueId, LogEntry logEntry, boolean isConfidential) {
        return createBaseRequest("/issues/" + issueId + "/notes").post(createIssueCommentRequestBody(logEntry, isConfidential)).build();
    }

    private RequestBody createIssueCommentRequestBody(LogEntry logEntry, boolean isConfidential) {
        JSONObject json = new JSONObject()
                .put("body", createDescription(logEntry))
                .put("confidential", confidential && !isConfidential);
        return RequestBody.create(AbstractWebAppender.APPLICATION_JSON, json.toString());
    }
}
