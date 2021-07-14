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

import com.github.taucher2003.appenders.utils.Utilities;
import org.slf4j.Marker;
import org.slf4j.helpers.MessageFormatter;

import java.util.Arrays;

public final class LogEntry {

    private final String threadName;
    private final LogLevel level;
    private final String messageFormat;
    private final String formattedMessage;
    private final Object[] argumentArray;
    private final String loggerName;
    private final Throwable throwable;
    private final Marker marker;
    private final long timestamp;

    private LogEntry(String threadName, LogLevel level, String messageFormat, Object[] argumentArray, String formattedMessage,
                     String loggerName, Throwable throwable, Marker marker, long timestamp) {
        this.threadName = threadName;
        this.level = level;
        this.messageFormat = messageFormat;
        this.argumentArray = argumentArray;
        this.loggerName = loggerName;
        this.throwable = throwable;
        this.marker = marker;
        this.timestamp = timestamp;
        this.formattedMessage = Utilities.firstNotNull(formattedMessage, MessageFormatter.arrayFormat(messageFormat, argumentArray).getMessage());
    }

    public String getThreadName() {
        return threadName;
    }

    public LogLevel getLevel() {
        return level;
    }

    public String getMessageFormat() {
        return messageFormat;
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

    @Override
    public String toString() {
        return "LogEntry{" +
                "threadName='" + threadName + '\'' +
                ", level=" + level +
                ", messageFormat='" + messageFormat + '\'' +
                ", formattedMessage='" + formattedMessage + '\'' +
                ", argumentArray=" + Arrays.toString(argumentArray) +
                ", loggerName='" + loggerName + '\'' +
                ", throwable=" + throwable +
                ", marker=" + marker +
                ", timestamp=" + timestamp +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String threadName;
        private LogLevel level;
        private String message;
        private Object[] argumentArray;
        private String formattedMessage;
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

        public Builder formattedMessage(String formattedMessage) {
            this.formattedMessage = formattedMessage;
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
            return new LogEntry(threadName, level, message, argumentArray, formattedMessage, loggerName, throwable, marker, timestamp);
        }

        @Override
        public String toString() {
            return "Builder{" +
                    "threadName='" + threadName + '\'' +
                    ", level=" + level +
                    ", message='" + message + '\'' +
                    ", argumentArray=" + Arrays.toString(argumentArray) +
                    ", formattedMessage='" + formattedMessage + '\'' +
                    ", loggerName='" + loggerName + '\'' +
                    ", throwable=" + throwable +
                    ", marker=" + marker +
                    ", timestamp=" + timestamp +
                    '}';
        }
    }
}
