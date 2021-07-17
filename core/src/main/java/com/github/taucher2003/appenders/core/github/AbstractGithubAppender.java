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
import com.github.taucher2003.appenders.utils.DataPair;
import com.github.taucher2003.appenders.utils.executors.GithubWebRequestExecutor;
import okhttp3.Request;

import java.util.concurrent.TimeUnit;

public abstract class AbstractGithubAppender extends AbstractWebAppender {

    protected String repositoryOwner;
    protected String repositoryName;
    protected String accessToken;
    protected DataPair<Integer, TimeUnit> maxExecutionTime = new DataPair<>(20, TimeUnit.SECONDS);

    protected AbstractGithubAppender() {
        super(new GithubWebRequestExecutor(SELF_IGNORE_MARKER, HTTP_CLIENT));
    }

    @Override
    protected DataPair<Integer, TimeUnit> maxExecutionTime() {
        return maxExecutionTime;
    }

    protected String assembleRepositoryUrl() {
        return baseUrl + "/repos/" + repositoryOwner + "/" + repositoryName;
    }

    protected Request.Builder createBaseRequest(String repoPath) {
        return new Request.Builder()
                .url(assembleRepositoryUrl() + repoPath)
                .header("Authorization", "token " + accessToken)
                .header("Accept", "application/vnd.github.v3+json");
    }

    // ---- Setters

    public void setRepositoryOwner(String repositoryOwner) {
        this.repositoryOwner = repositoryOwner;
    }

    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
