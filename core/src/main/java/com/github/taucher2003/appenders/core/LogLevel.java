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

public class LogLevel {

    public static final LogLevel
            ERROR = new LogLevel(40000, "ERROR"),
            WARN = new LogLevel(30000, "WARN"),
            INFO = new LogLevel(20000, "INFO"),
            DEBUG = new LogLevel(10000, "DEBUG"),
            TRACE = new LogLevel(5000, "TRACE"),
            OFF = new LogLevel(0, "OFF");

    private final int level;
    private final String levelName;

    public LogLevel(int level, String levelName) {
        this.level = level;
        this.levelName = levelName;
    }

    public int getLevel() {
        return level;
    }

    public String getLevelName() {
        return levelName;
    }

    @Override
    public String toString() {
        return getLevelName();
    }

    public static LogLevel fromString(String name) {
        return fromString(name, DEBUG);
    }

    public static LogLevel fromString(String name, LogLevel fallback) {
        if (name == null) {
            return fallback;
        }

        if ("ERROR".equalsIgnoreCase(name)) {
            return ERROR;
        }
        if ("WARN".equalsIgnoreCase(name)) {
            return WARN;
        }
        if ("INFO".equalsIgnoreCase(name)) {
            return INFO;
        }
        if ("DEBUG".equalsIgnoreCase(name)) {
            return DEBUG;
        }
        if ("TRACE".equalsIgnoreCase(name)) {
            return TRACE;
        }
        if ("OFF".equalsIgnoreCase(name)) {
            return OFF;
        }
        return fallback;
    }
}
