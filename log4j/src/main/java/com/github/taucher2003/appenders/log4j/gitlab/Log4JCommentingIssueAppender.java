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

package com.github.taucher2003.appenders.log4j.gitlab;

import com.github.taucher2003.appenders.core.gitlab.CommentingIssueAppender;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

@Plugin(name = "GitlabIssue", category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE)
public final class Log4JCommentingIssueAppender extends AbstractLog4JGitlabAppender<CommentingIssueAppender> {

    // legacy support for log4j version before 2.11.2
    @Deprecated
    private Log4JCommentingIssueAppender(String name, String baseUrl, String repositoryId, String accessToken, boolean confidential, Filter filter, boolean ignoreExceptions, Object ignored) {
        super(new CommentingIssueAppender(), name, baseUrl, repositoryId, accessToken, filter, null, ignoreExceptions);
        delegate.setConfidential(confidential);
    }

    private Log4JCommentingIssueAppender(String name, String baseUrl, String repositoryId, String accessToken, boolean confidential, Filter filter, boolean ignoreExceptions) {
        super(new CommentingIssueAppender(), baseUrl, repositoryId, accessToken, name, filter, null, ignoreExceptions, null);
        delegate.setConfidential(confidential);
    }

    @PluginFactory
    public static Log4JCommentingIssueAppender createAppender(
            @PluginAttribute("name") String name,
            @PluginAttribute("ignoreExceptions") boolean ignoreExceptions,
            @PluginAttribute(value = "baseUrl", defaultString = "https://api.github.com") String baseUrl,
            @PluginAttribute(value = "repositoryId") String repositoryId,
            @PluginAttribute(value = "accessToken", sensitive = true) String accessToken,
            @PluginAttribute(value = "confidential") boolean confidential,
            @PluginElement("Filters") Filter filter
    ) {
        if (name == null) {
            LOGGER.error("no name provided");
            return null;
        }

        try {
            return new Log4JCommentingIssueAppender(name, baseUrl, repositoryId, accessToken, confidential, filter, ignoreExceptions);
        } catch (NoSuchMethodError ignored) {
            return new Log4JCommentingIssueAppender(name, baseUrl, repositoryId, accessToken, confidential, filter, ignoreExceptions, null);
        }
    }
}
