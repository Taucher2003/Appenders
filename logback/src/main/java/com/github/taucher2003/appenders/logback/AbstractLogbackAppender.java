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

package com.github.taucher2003.appenders.logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxy;
import ch.qos.logback.core.AppenderBase;
import com.github.taucher2003.appenders.core.AbstractAppender;
import com.github.taucher2003.appenders.core.LogEntry;
import com.github.taucher2003.appenders.core.LogLevel;

public abstract class AbstractLogbackAppender<T extends AbstractAppender> extends AppenderBase<ILoggingEvent> {

    protected final T delegate;

    protected AbstractLogbackAppender(T delegate) {
        this.delegate = delegate;
    }

    @Override
    protected void append(ILoggingEvent eventObject) {
        LogEntry.Builder builder = LogEntry.builder()
                .threadName(eventObject.getThreadName())
                .level(fromLogback(eventObject.getLevel()))
                .message(eventObject.getMessage())
                .argumentArray(eventObject.getArgumentArray())
                .loggerName(eventObject.getLoggerName());
        if (eventObject.getThrowableProxy() != null) {
            builder.throwable(((ThrowableProxy) eventObject.getThrowableProxy()).getThrowable());
        }
        builder.marker(eventObject.getMarker())
                .timestamp(eventObject.getTimeStamp())
                .build();
        delegate.append(builder.build());
    }

    private LogLevel fromLogback(Level level) {
        return LogLevel.fromString(level.levelStr, LogLevel.DEBUG);
    }

    public void setFlushInterval(String flushInterval) {
        delegate.setFlushInterval(flushInterval);
    }

    public void setFlushUnit(String flushUnit) {
        delegate.setFlushUnit(flushUnit);
    }

    // actually adds a marker
    public void setMarker(String marker) {
        delegate.setMarker(marker);
    }

    // actually adds an ignored marker
    public void setIgnoredMarker(String marker) {
        delegate.setIgnoredMarker(marker);
    }

    // actually adds a level
    public void setLevel(String level) {
        delegate.setLevel(level);
    }
}
