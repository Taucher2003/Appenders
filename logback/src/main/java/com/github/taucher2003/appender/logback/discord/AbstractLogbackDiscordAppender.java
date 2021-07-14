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

package com.github.taucher2003.appender.logback.discord;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.github.taucher2003.appender.logback.AbstractLogbackAppender;
import com.github.taucher2003.appenders.core.LogEntry;
import com.github.taucher2003.appenders.core.LogLevel;
import com.github.taucher2003.appenders.core.discord.AbstractDiscordAppender;

public abstract class AbstractLogbackDiscordAppender<T extends AbstractDiscordAppender> extends AbstractLogbackAppender<T> {
    protected AbstractLogbackDiscordAppender(T delegate) {
        super(delegate);
    }

    @Override
    protected void append(ILoggingEvent eventObject) {
        LogEntry entry = LogEntry.builder()
                .threadName(eventObject.getThreadName())
                .level(fromLogback(eventObject.getLevel()))
                .message(eventObject.getMessage())
                .argumentArray(eventObject.getArgumentArray())
                .loggerName(eventObject.getLoggerName())
                .throwable((Throwable) eventObject.getThrowableProxy())
                .marker(eventObject.getMarker())
                .timestamp(eventObject.getTimeStamp())
                .build();
        delegate.append(entry);
    }

    private LogLevel fromLogback(Level level) {
        return LogLevel.fromString(level.levelStr, LogLevel.DEBUG);
    }

    public void setErrorColor(String color) {
        delegate.setErrorColor(color);
    }

    public void setWarnColor(String color) {
        delegate.setWarnColor(color);
    }

    public void setInfoColor(String color) {
        delegate.setInfoColor(color);
    }

    public void setDebugColor(String color) {
        delegate.setDebugColor(color);
    }

    public void setTraceColor(String color) {
        delegate.setTraceColor(color);
    }

    public void setFallbackColor(String color) {
        delegate.setFallbackColor(color);
    }
}