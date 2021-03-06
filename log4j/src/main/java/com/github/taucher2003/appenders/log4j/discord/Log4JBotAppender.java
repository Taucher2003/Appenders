/*
 *
 *  Copyright 2022 Niklas van Schrick and the contributors of the Appenders Project
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

import com.github.taucher2003.appenders.core.discord.BotAppender;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Plugin(name = "DiscordBot", category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE)
public final class Log4JBotAppender extends AbstractLog4JDiscordAppender<BotAppender> {

    private static final Logger LOGGER = LoggerFactory.getLogger(Log4JBotAppender.class);

    // legacy support for log4j version before 2.11.2
    @Deprecated
    private Log4JBotAppender(String name, String token, String channelId, String apiHost, Filter filter, boolean ignoreExceptions, Object ignored) {
        super(new BotAppender(), name, filter, null, ignoreExceptions);
        delegate.setToken(token);
        delegate.setChannelId(channelId);
        if(!"".equalsIgnoreCase(apiHost)) {
            delegate.setApiHost(apiHost);
        }
    }

    private Log4JBotAppender(String name, String token, String channelId, String apiHost, Filter filter, boolean ignoreExceptions) {
        super(new BotAppender(), name, filter, null, ignoreExceptions, null);
        delegate.setToken(token);
        delegate.setChannelId(channelId);
        if(!"".equalsIgnoreCase(apiHost)) {
            delegate.setApiHost(apiHost);
        }
    }

    @PluginFactory
    public static Log4JBotAppender createAppender(
            @PluginAttribute("name") String name,
            @PluginAttribute("ignoreExceptions") boolean ignoreExceptions,
            @PluginAttribute(value = "token", sensitive = true) String token,
            @PluginAttribute(value = "channelId") String channelId,
            @PluginAttribute(value = "apiHost") String apiHost,
            @PluginElement("Filters") Filter filter
    ) {
        if (name == null) {
            LOGGER.error("no name provided");
            return null;
        }

        try {
            return new Log4JBotAppender(name, token, channelId, apiHost, filter, ignoreExceptions);
        } catch (NoSuchMethodError ignored) {
            return new Log4JBotAppender(name, token, channelId, apiHost, filter, ignoreExceptions, null);
        }
    }
}