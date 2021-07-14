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

import com.github.taucher2003.appenders.core.github.IssueAppender;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

@Plugin(name = "GithubIssue", category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE)
public final class Log4JIssueAppender extends AbstractLog4JGithubAppender<IssueAppender> {

    // legacy support for log4j version before 2.11.2
    @Deprecated
    private Log4JIssueAppender(String name, String baseUrl, String repositoryOwner, String repositoryName, String accessToken, Filter filter, boolean ignoreExceptions, Object ignored) {
        super(new IssueAppender(), name, baseUrl, repositoryOwner, repositoryName, accessToken, filter, null, ignoreExceptions);
    }

    private Log4JIssueAppender(String name, String baseUrl, String repositoryOwner, String repositoryName, String accessToken, Filter filter, boolean ignoreExceptions) {
        super(new IssueAppender(), baseUrl, repositoryOwner, repositoryName, accessToken, name, filter, null, ignoreExceptions, null);
    }

    @PluginFactory
    public static Log4JIssueAppender createAppender(
            @PluginAttribute("name") String name,
            @PluginAttribute("ignoreExceptions") boolean ignoreExceptions,
            @PluginAttribute(value = "baseUrl", sensitive = true) String baseUrl,
            @PluginAttribute(value = "repositoryOwner", sensitive = true) String repositoryOwner,
            @PluginAttribute(value = "repositoryName", sensitive = true) String repositoryName,
            @PluginAttribute(value = "accessToken", sensitive = true) String accessToken,
            @PluginElement("Filters") Filter filter
    ) {
        if (name == null) {
            LOGGER.error("no name provided");
            return null;
        }

        try {
            return new Log4JIssueAppender(name, baseUrl, repositoryOwner, repositoryName, accessToken, filter, ignoreExceptions);
        } catch (NoSuchMethodError ignored) {
            return new Log4JIssueAppender(name, baseUrl, repositoryOwner, repositoryName, accessToken, filter, ignoreExceptions, null);
        }
    }
}
