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

package com.github.taucher2003.appenders.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

public abstract class AbstractAppender {

    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractAppender.class);
    protected static final ForkJoinPool FORK_JOIN_POOL = ForkJoinPool.commonPool();
    protected static final Collection<AbstractAppender> INSTANCES = new CopyOnWriteArrayList<>();
    protected static final Marker SELF_IGNORE_MARKER = MarkerFactory.getMarker(AbstractAppender.class.getCanonicalName());

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(AbstractAppender::shutdown));
    }

    private static void shutdown() {
        INSTANCES.forEach(AbstractAppender::stop);
        //noinspection ResultOfMethodCallIgnored // we only need the side effect
        FORK_JOIN_POOL.awaitQuiescence(1, TimeUnit.SECONDS);
    }

    private int flushInterval = 5;
    private TimeUnit flushUnit = TimeUnit.SECONDS;

    private final Collection<String> markerNames = new CopyOnWriteArrayList<>();
    private final Collection<String> ignoredMarkerNames = new CopyOnWriteArrayList<>();
    private final Collection<LogLevel> levels = new CopyOnWriteArrayList<>();

    private boolean isStarted;

    protected AbstractAppender() {
        INSTANCES.add(this);
    }

    public void start() {
        isStarted = true;
        startInternally();
        FORK_JOIN_POOL.execute(this::processFlush);
    }

    private void processFlush() {
        try {
            Thread.sleep(flushUnit.toMillis(flushInterval));
        } catch (InterruptedException e) {
            LOGGER.warn(SELF_IGNORE_MARKER, "Failed to wait for next flush", e);
        }
        flush();
        if(isStarted) {
            FORK_JOIN_POOL.execute(this::processFlush);
        }
    }

    public void stop() {
        flush();
        stopInternally();
        isStarted = false;
    }

    public final void append(LogEntry<?> logEntry) {
        if (logEntry.getMarker() != null && logEntry.getMarker().contains(SELF_IGNORE_MARKER)) {
            return;
        }
        if (logEntry.getMarker() != null) {
            if (!markerNames.isEmpty()
                    && !markerNames.contains(logEntry.getMarker().getName())) {
                return;
            }

            if (ignoredMarkerNames.contains(logEntry.getMarker().getName())) {
                return;
            }
        }
        if (logEntry.getMarker() == null
                && !markerNames.isEmpty()) {
            return;
        }
        if (!levels.isEmpty()
                && !levels.contains(logEntry.getLevel())) {
            return;
        }

        doAppend(logEntry);
    }

    protected abstract void doAppend(LogEntry<?> logEntry);

    protected abstract void startInternally();

    protected abstract void stopInternally();

    protected abstract void flush();

    // ---- Setters

    public void setFlushInterval(String flushInterval) {
        this.flushInterval = Integer.parseInt(flushInterval);
    }

    public void setFlushUnit(String flushUnit) {
        this.flushUnit = TimeUnit.valueOf(flushUnit);
    }

    // actually adds a marker
    public void setMarker(String marker) {
        markerNames.add(marker);
    }

    // actually adds an ignored marker
    public void setIgnoredMarker(String marker) {
        ignoredMarkerNames.add(marker);
    }

    // actually adds a level
    public void setLevel(String level) {
        levels.add(LogLevel.fromString(level, LogLevel.OFF));
    }
}
