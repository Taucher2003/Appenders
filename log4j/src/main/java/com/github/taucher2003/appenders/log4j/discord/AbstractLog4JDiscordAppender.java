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

package com.github.taucher2003.appenders.log4j.discord;

import com.github.taucher2003.appenders.core.LogEntry;
import com.github.taucher2003.appenders.core.LogLevel;
import com.github.taucher2003.appenders.core.discord.AbstractDiscordAppender;
import com.github.taucher2003.appenders.log4j.AbstractLog4JAppender;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Property;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

public abstract class AbstractLog4JDiscordAppender<T extends AbstractDiscordAppender> extends AbstractLog4JAppender<T> {

    protected AbstractLog4JDiscordAppender(T delegate, String name, Filter filter, Layout<? extends Serializable> layout, boolean ignoreExceptions, Property[] properties) {
        super(delegate, name, filter, layout, ignoreExceptions, properties);
    }

    @Override
    public void append(LogEvent eventObject) {
        LogEntry entry = LogEntry.builder()
                .threadName(eventObject.getThreadName())
                .level(fromLog4J(eventObject.getLevel()))
                .message(eventObject.getMessage().getFormat())
                .argumentArray(eventObject.getMessage().getParameters())
                .formattedMessage(eventObject.getMessage().getFormattedMessage())
                .loggerName(eventObject.getLoggerName())
                .throwable(eventObject.getThrown())
                .marker(fromLog4J(eventObject.getMarker()))
                .timestamp(eventObject.getTimeMillis())
                .build();
        delegate.append(entry);
    }

    private LogLevel fromLog4J(Level level) {
        return LogLevel.fromString(level.name(), LogLevel.DEBUG);
    }

    private Marker fromLog4J(org.apache.logging.log4j.Marker marker) {
        return MarkerFactory.getMarker(marker.getName());
    }

    @Override
    public void start() {
        super.start();
        delegate.start();
    }

    @Override
    public boolean stop(long timeout, TimeUnit timeUnit) {
        delegate.stop();
        return super.stop(timeout, timeUnit);
    }
}
