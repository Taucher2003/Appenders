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
import com.github.taucher2003.appenders.utils.Utilities;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class IssueAppender extends AbstractGitlabAppender {

    // issue create has an actual limit of 1_048_576
    private static final int DESCRIPTION_CHARACTER_LIMIT = 1_000_000;

    protected List<String> labels = new ArrayList<>();
    protected boolean confidential;

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
                .put("description", createDescription(logEntry))
                .put("labels", labels)
                .put("confidential", confidential);
        return RequestBody.create(AbstractWebAppender.APPLICATION_JSON, object.toString());
    }

    protected String createDescription(LogEntry logEntry) {
        String base = "An exception has been logged with "
                + logEntry.getLevel().getLevelName()
                + " level.\n\n"
                + logEntry.getFormattedMessage()
                + "\n\n```\n"
                + "%s"
                + "```";
        String exception = Utilities.getExceptionStacktrace(logEntry.getThrowable(), DESCRIPTION_CHARACTER_LIMIT - base.length());
        return String.format(base, exception);
    }

    // ---- Setters

    // actually adds a label
    public void setLabel(String label) {
        labels.add(label);
    }

    public void setConfidential(boolean confidential) {
        this.confidential = confidential;
    }
}
