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
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.core.AppenderBase;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import com.github.taucher2003.appenders.utils.Utilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.awt.Color;
import java.time.Instant;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public abstract class AbstractDiscordAppender extends AppenderBase<ILoggingEvent> {

    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractDiscordAppender.class);
    protected static final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newScheduledThreadPool(1);
    protected static final Collection<AbstractDiscordAppender> INSTANCES = new CopyOnWriteArrayList<>();
    protected static final Marker SELF_IGNORE_MARKER = MarkerFactory.getMarker(AbstractDiscordAppender.class.getCanonicalName());

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(AbstractDiscordAppender::shutdown));
    }

    private static void shutdown() {
        INSTANCES.forEach(AbstractDiscordAppender::stop);
        EXECUTOR_SERVICE.shutdown();
    }

    protected Consumer<Collection<WebhookEmbed>> sendStrategy;

    private int sendingInterval = 5;
    private TimeUnit sendingUnit = TimeUnit.SECONDS;

    private Color errorColor = new Color(0xFF0000);
    private Color warnColor = new Color(0xFFC800);
    private Color infoColor = new Color(0x32CD32);
    private Color debugColor = new Color(0x116DF6);
    private Color traceColor = new Color(0x0117C2);
    private Color fallbackColor = new Color(0x777777);

    private final Collection<WebhookEmbed> embedBuffer = new CopyOnWriteArrayList<>();
    private final Collection<String> markerNames = new CopyOnWriteArrayList<>();
    private final Collection<String> ignoredMarkerNames = new CopyOnWriteArrayList<>();
    private final Collection<Level> levels = new CopyOnWriteArrayList<>();

    private ScheduledFuture<?> sendFuture;

    protected AbstractDiscordAppender() {
        INSTANCES.add(this);
    }

    @Override
    public final void start() {
        startInternally();
        sendFuture = EXECUTOR_SERVICE.scheduleAtFixedRate(this::send, sendingInterval, sendingInterval, sendingUnit);
        super.start();
    }

    @Override
    public final void stop() {
        super.stop();
        if (sendFuture != null) {
            sendFuture.cancel(false);
        }
        send();
        stopInternally();
    }

    @Override
    public final void append(ILoggingEvent iLoggingEvent) {
        if (iLoggingEvent.getMarker() != null && iLoggingEvent.getMarker().contains(SELF_IGNORE_MARKER)) {
            return;
        }
        if (iLoggingEvent.getMarker() != null) {
            if (!markerNames.isEmpty()
                    && !markerNames.contains(iLoggingEvent.getMarker().getName())) {
                return;
            }

            if (ignoredMarkerNames.contains(iLoggingEvent.getMarker().getName())) {
                return;
            }
        }
        if (!levels.isEmpty()
                && !levels.contains(iLoggingEvent.getLevel())) {
            return;
        }

        WebhookEmbed embed = createEmbed(iLoggingEvent);
        add(embed);
    }

    protected WebhookEmbed createEmbed(ILoggingEvent iLoggingEvent) {
        WebhookEmbedBuilder builder = new WebhookEmbedBuilder()
                .setTitle(new WebhookEmbed.EmbedTitle(iLoggingEvent.getLevel().toString(), null))
                .setTimestamp(Instant.ofEpochMilli(iLoggingEvent.getTimeStamp()))
                .setColor(getColor(iLoggingEvent.getLevel()).getRGB())
                .setFooter(new WebhookEmbed.EmbedFooter(iLoggingEvent.getLoggerName() + "@" + iLoggingEvent.getThreadName(), null));

        String description = Utilities.shortenWithEndCut(iLoggingEvent.getFormattedMessage(), maxTextSize());

        IThrowableProxy throwable = iLoggingEvent.getThrowableProxy();
        if (throwable != null) {
            int remainingCharacters = maxTextSize() - description.length();

            remainingCharacters -= "\n\n``````".length();

            if (remainingCharacters > 0) {
                String stacktraceFormatted = Utilities.getExceptionStacktrace(throwable, remainingCharacters);
                description += String.format("\n\n```%s```", stacktraceFormatted);
            }
        }

        builder.setDescription(description);

        return builder.build();
    }

    protected Color getColor(Level level) {
        if (Level.TRACE.equals(level)) {
            return traceColor;
        }
        if (Level.DEBUG.equals(level)) {
            return debugColor;
        }
        if (Level.INFO.equals(level)) {
            return infoColor;
        }
        if (Level.WARN.equals(level)) {
            return warnColor;
        }
        if (Level.ERROR.equals(level)) {
            return errorColor;
        }
        return fallbackColor;
    }

    protected void add(WebhookEmbed embed) {
        synchronized (embedBuffer) {
            embedBuffer.add(embed);

            if (embedBuffer.size() >= maxBufferSize()) {
                send();
            }
        }
    }

    protected void send() {
        synchronized (embedBuffer) {
            if (embedBuffer.isEmpty()) {
                return;
            }

            sendStrategy.accept(embedBuffer);
            embedBuffer.clear();
        }
    }

    protected abstract void startInternally();

    protected abstract void stopInternally();

    protected abstract int maxBufferSize();

    protected abstract int maxTextSize();


    // ---- Setters

    public void setErrorColor(String color) {
        this.errorColor = new Color(Integer.parseInt(color));
    }

    public void setWarnColor(String color) {
        this.warnColor = new Color(Integer.parseInt(color));
    }

    public void setInfoColor(String color) {
        this.infoColor = new Color(Integer.parseInt(color));
    }

    public void setDebugColor(String color) {
        this.debugColor = new Color(Integer.parseInt(color));
    }

    public void setTraceColor(String color) {
        this.traceColor = new Color(Integer.parseInt(color));
    }

    public void setFallbackColor(String color) {
        this.fallbackColor = new Color(Integer.parseInt(color));
    }

    public void setSendingInterval(String sendingInterval) {
        this.sendingInterval = Integer.parseInt(sendingInterval);
    }

    public void setSendingUnit(String sendingUnit) {
        this.sendingUnit = TimeUnit.valueOf(sendingUnit);
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
        levels.add(Level.toLevel(level, Level.OFF));
    }
}
