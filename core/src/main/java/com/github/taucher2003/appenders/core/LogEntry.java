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

package com.github.taucher2003.appenders.core;

import org.slf4j.Marker;
import org.slf4j.helpers.MessageFormatter;

public final class LogEntry {

    private final String threadName;
    private final LogLevel level;
    private final String message;
    private final String formattedMessage;
    private final Object[] argumentArray;
    private final String loggerName;
    private final Throwable throwable;
    private final Marker marker;
    private final long timestamp;

    private LogEntry(String threadName, LogLevel level, String message, Object[] argumentArray, String loggerName,
                     Throwable throwable, Marker marker, long timestamp) {
        this.threadName = threadName;
        this.level = level;
        this.message = message;
        this.argumentArray = argumentArray;
        this.loggerName = loggerName;
        this.throwable = throwable;
        this.marker = marker;
        this.timestamp = timestamp;
        this.formattedMessage = MessageFormatter.arrayFormat(message, argumentArray).getMessage();
    }

    public String getThreadName() {
        return threadName;
    }

    public LogLevel getLevel() {
        return level;
    }

    public String getMessage() {
        return message;
    }

    public String getFormattedMessage() {
        return formattedMessage;
    }

    public Object[] getArgumentArray() {
        return argumentArray;
    }

    public String getLoggerName() {
        return loggerName;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public Marker getMarker() {
        return marker;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String threadName;
        private LogLevel level;
        private String message;
        private Object[] argumentArray;
        private String loggerName;
        private Throwable throwable;
        private Marker marker;
        private long timestamp;

        public Builder threadName(String threadName) {
            this.threadName = threadName;
            return this;
        }

        public Builder level(LogLevel level) {
            this.level = level;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder argumentArray(Object[] argumentArray) {
            this.argumentArray = argumentArray;
            return this;
        }

        public Builder loggerName(String loggerName) {
            this.loggerName = loggerName;
            return this;
        }

        public Builder throwable(Throwable throwable) {
            this.throwable = throwable;
            return this;
        }

        public Builder marker(Marker marker) {
            this.marker = marker;
            return this;
        }

        public Builder timestamp(long timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public LogEntry build() {
            return new LogEntry(threadName, level, message, argumentArray, loggerName, throwable, marker, timestamp);
        }
    }
}
