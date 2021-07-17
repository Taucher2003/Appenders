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

package com.github.taucher2003.appenders.log4j.github;

import com.github.taucher2003.appenders.core.github.AbstractGithubAppender;
import com.github.taucher2003.appenders.log4j.AbstractLog4JWebAppender;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.config.Property;

import java.io.Serializable;

public abstract class AbstractLog4JGithubAppender<T extends AbstractGithubAppender> extends AbstractLog4JWebAppender<T> {

    // legacy support for log4j version before 2.11.2
    @Deprecated
    protected AbstractLog4JGithubAppender(T delegate, String name, String baseUrl, String repositoryOwner, String repositoryName, String accessToken,
                                          Filter filter, Layout<? extends Serializable> layout, boolean ignoreExceptions) {
        super(delegate, name, baseUrl, filter, layout, ignoreExceptions);
        delegate.setRepositoryOwner(repositoryOwner);
        delegate.setRepositoryName(repositoryName);
        delegate.setAccessToken(accessToken);
    }

    protected AbstractLog4JGithubAppender(T delegate, String name, String baseUrl, String repositoryOwner, String repositoryName, String accessToken,
                                          Filter filter, Layout<? extends Serializable> layout, boolean ignoreExceptions, Property[] properties) {
        super(delegate, name, baseUrl, filter, layout, ignoreExceptions, properties);
        delegate.setRepositoryOwner(repositoryOwner);
        delegate.setRepositoryName(repositoryName);
        delegate.setAccessToken(accessToken);
    }
}