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

package com.github.taucher2003.appenders.core.github;

import com.github.taucher2003.appenders.core.AbstractWebAppender;
import com.github.taucher2003.appenders.core.LogEntry;
import com.github.taucher2003.appenders.utils.Utilities;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class IssueAppender extends AbstractGithubAppender {

    protected List<String> labels = new ArrayList<>();

    public IssueAppender() {
        labels.add("logger");
    }

    @Override
    protected void startInternally() {
    }

    @Override
    protected void stopInternally() {
    }

    @Override
    protected void doAppend(LogEntry logEntry) {
        if (logEntry.getThrowable() == null) {
            return;
        }

        super.doAppend(logEntry);
    }

    @Override
    protected Request createRequest(LogEntry logEntry) {
        return createBaseRequest("/issues")
                .post(createIssueBody(logEntry))
                .build();
    }

    private RequestBody createIssueBody(LogEntry logEntry) {
        JSONObject object = new JSONObject()
                .put("title", logEntry.getThrowable().toString())
                .put("body", createBody(logEntry))
                .put("labels", labels);
        return RequestBody.create(AbstractWebAppender.APPLICATION_JSON, object.toString());
    }

    protected String createBody(LogEntry logEntry) {
        return "["
                + logEntry.getLevel().getLevelName()
                + "] "
                + logEntry.getFormattedMessage()
                + "\n\n```\n"
                + Utilities.getExceptionStacktrace(logEntry.getThrowable())
                + "```";
    }

    // ---- Setters

    // actually adds a label
    public void setLabel(String label) {
        labels.add(label);
    }
}
