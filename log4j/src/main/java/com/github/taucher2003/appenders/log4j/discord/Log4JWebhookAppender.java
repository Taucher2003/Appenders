/*
 *
 *  Copyright 2023 Niklas van Schrick and the contributors of the Appenders Project
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

package com.github.taucher2003.appenders.log4j.discord;

import com.github.taucher2003.appenders.core.discord.WebhookAppender;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Plugin(name = "DiscordWebhook", category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE)
public final class Log4JWebhookAppender extends AbstractLog4JDiscordAppender<WebhookAppender> {

    private static final Logger LOGGER = LoggerFactory.getLogger(Log4JBotAppender.class);

    // legacy support for log4j version before 2.11.2
    @Deprecated
    private Log4JWebhookAppender(String name, String url, long threadId, String username, String avatarUrl, Filter filter, boolean ignoreExceptions, Object ignored) {
        super(new WebhookAppender(), name, filter, null, ignoreExceptions);
        delegate.setUrl(url);
        delegate.setThreadId(threadId);
        delegate.setUsername(username);
        delegate.setAvatarUrl(avatarUrl);
    }

    private Log4JWebhookAppender(String name, String url, long threadId, String username, String avatarUrl, Filter filter, boolean ignoreExceptions) {
        super(new WebhookAppender(), name, filter, null, ignoreExceptions, null);
        delegate.setUrl(url);
        delegate.setThreadId(threadId);
        delegate.setUsername(username);
        delegate.setAvatarUrl(avatarUrl);
    }

    @PluginFactory
    public static Log4JWebhookAppender createAppender(
            @PluginAttribute("name") String name,
            @PluginAttribute("ignoreExceptions") boolean ignoreExceptions,
            @PluginAttribute(value = "url", sensitive = true) String url,
            @PluginAttribute("threadId") long threadId,
            @PluginAttribute(value = "username") String username,
            @PluginAttribute("avatarUrl") String avatarUrl,
            @PluginElement("Filters") Filter filter
    ) {
        if (name == null) {
            LOGGER.error("no name provided");
            return null;
        }

        if(username != null && username.isEmpty()) {
            username = null;
        }
        if(avatarUrl != null && avatarUrl.isEmpty()) {
            avatarUrl = null;
        }

        try {
            return new Log4JWebhookAppender(name, url, threadId, username, avatarUrl, filter, ignoreExceptions);
        } catch (NoSuchMethodError ignored) {
            return new Log4JWebhookAppender(name, url, threadId, username, avatarUrl, filter, ignoreExceptions, null);
        }
    }
}
