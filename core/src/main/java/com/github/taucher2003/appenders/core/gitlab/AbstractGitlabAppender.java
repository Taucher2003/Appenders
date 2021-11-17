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
import com.github.taucher2003.appenders.utils.DataPair;
import com.github.taucher2003.appenders.utils.executors.GitlabWebRequestExecutor;
import okhttp3.Request;

import java.util.concurrent.TimeUnit;

public abstract class AbstractGitlabAppender extends AbstractWebAppender {

    protected int repositoryId;
    protected String accessToken;
    protected DataPair<Integer, TimeUnit> maxExecutionTime = new DataPair<>(40, TimeUnit.SECONDS);

    protected AbstractGitlabAppender() {
        super(new GitlabWebRequestExecutor(SELF_IGNORE_MARKER, HTTP_CLIENT));
    }

    @Override
    protected DataPair<Integer, TimeUnit> maxExecutionTime() {
        return maxExecutionTime;
    }

    protected String assembleRepositoryUrl() {
        return super.baseUrl + "/api/v4/projects/" + repositoryId;
    }

    protected Request.Builder createBaseRequest(String repoPath) {
        return new Request.Builder()
                .url(assembleRepositoryUrl() + repoPath)
                .header("Private-Token", accessToken)
                .header("Accept", "application/json");
    }

    // ---- Setters

    public void setRepositoryId(String repositoryId) {
        this.repositoryId = Integer.parseInt(repositoryId);
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
