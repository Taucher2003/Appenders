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

package com.github.taucher2003.appenders.logback.log;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import com.github.taucher2003.appenders.core.LogEntry;
import com.github.taucher2003.appenders.core.log.CallbackAppender;
import com.github.taucher2003.appenders.core.log.PassthroughAppender;
import com.github.taucher2003.appenders.logback.AbstractLogbackAppender;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

public class LogbackPassthroughAppender extends AbstractLogbackAppender<PassthroughAppender<ILoggingEvent>> implements CallbackAppender<ILoggingEvent> {
    public LogbackPassthroughAppender() {
        super(new PassthroughAppender<>());
        delegate.setDelegate(this);
    }

    @Override
    public void call(LogEntry<? extends ILoggingEvent> entry) {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        for (Logger logger : context.getLoggerList()) {
            // this looks bad, but there is no better option
            Iterator<Appender<ILoggingEvent>> it = logger.iteratorForAppenders();
            while (it.hasNext()) {
                Appender<ILoggingEvent> appender = it.next();
                if (!appender.getName().equalsIgnoreCase(delegate.getTargetAppenderName())) {
                    continue;
                }
                appender.doAppend(entry.getT());
                // we called the appender, nothing more to do
                return;
            }
        }
    }

    // --- setters

    // used by logback, not my interface
    @SuppressWarnings("PublicMethodNotExposedInInterface")
    public void setTargetAppenderName(String targetAppenderName) {
        delegate.setTargetAppenderName(targetAppenderName);
    }
}
