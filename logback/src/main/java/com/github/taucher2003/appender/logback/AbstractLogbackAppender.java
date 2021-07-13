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

package com.github.taucher2003.appender.logback;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import com.github.taucher2003.appenders.core.AbstractAppender;

public abstract class AbstractLogbackAppender<T extends AbstractAppender> extends AppenderBase<ILoggingEvent> {

    protected final T delegate;

    protected AbstractLogbackAppender(T delegate) {
        this.delegate = delegate;
    }

    @Override
    protected abstract void append(ILoggingEvent eventObject);

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
