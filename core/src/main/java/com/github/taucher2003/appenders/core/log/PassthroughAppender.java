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

package com.github.taucher2003.appenders.core.log;

import com.github.taucher2003.appenders.core.AbstractAppender;
import com.github.taucher2003.appenders.core.LogEntry;

public class PassthroughAppender<T> extends AbstractAppender {

    private CallbackAppender<T> delegate;
    private String targetAppenderName;

    @Override
    protected void doAppend(LogEntry<?> logEntry) {
        // all checks have been made by AbstractAppender
        // we can call the delegated appender directly

        //noinspection unchecked
        delegate.call((LogEntry<T>) logEntry);
    }

    @Override
    protected void startInternally() {
        // no-op
    }

    @Override
    protected void stopInternally() {
        // no-op
    }

    @Override
    protected void flush() {
        // no-op
    }

    public String getTargetAppenderName() {
        return targetAppenderName;
    }

    // internal use only
    public void setDelegate(CallbackAppender<T> delegate) {
        this.delegate = delegate;
    }

    // --- setters

    public void setTargetAppenderName(String targetAppenderName) {
        this.targetAppenderName = targetAppenderName;
    }
}
