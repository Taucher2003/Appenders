/*
 *
 *  Copyright 2023 Niklas van Schrick and the contributors of the Appenders Project
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

package com.github.taucher2003.appenders.core.discord;

import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import com.github.taucher2003.appenders.core.AbstractAppender;
import com.github.taucher2003.appenders.core.LogEntry;
import com.github.taucher2003.appenders.core.LogLevel;
import com.github.taucher2003.appenders.utils.Utilities;

import java.awt.Color;
import java.time.Instant;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public abstract class AbstractDiscordAppender extends AbstractAppender {

    protected Consumer<Collection<WebhookEmbed>> sendStrategy;

    private Color errorColor = new Color(0xFF0000);
    private Color warnColor = new Color(0xFFC800);
    private Color infoColor = new Color(0x32CD32);
    private Color debugColor = new Color(0x116DF6);
    private Color traceColor = new Color(0x0117C2);
    private Color fallbackColor = new Color(0x777777);

    private final Collection<WebhookEmbed> embedBuffer = new CopyOnWriteArrayList<>();

    protected AbstractDiscordAppender() {
        INSTANCES.add(this);
    }

    @Override
    public final void start() {
        super.start();
        startInternally();
    }

    @Override
    public final void stop() {
        super.stop();
        stopInternally();
    }

    @Override
    public final void doAppend(LogEntry<?> logEntry) {
        WebhookEmbed embed = createEmbed(logEntry);
        add(embed);
    }

    protected WebhookEmbed createEmbed(LogEntry<?> logEntry) {
        WebhookEmbedBuilder builder = new WebhookEmbedBuilder()
                .setTitle(new WebhookEmbed.EmbedTitle(logEntry.getLevel().toString(), null))
                .setTimestamp(Instant.ofEpochMilli(logEntry.getTimestamp()))
                .setColor(getColor(logEntry.getLevel()).getRGB())
                .setFooter(new WebhookEmbed.EmbedFooter(logEntry.getLoggerName() + "@" + logEntry.getThreadName(), null));

        String description = Utilities.shortenWithEndCut(logEntry.getFormattedMessage(), maxTextSize());

        Throwable throwable = logEntry.getThrowable();
        if (throwable != null) {
            int remainingCharacters = maxTextSize() - description.length();

            remainingCharacters -= "\n\n```\n```".length();

            if (remainingCharacters > 0) {
                String stacktraceFormatted = Utilities.getExceptionStacktrace(throwable, remainingCharacters);
                description += String.format("\n\n```\n%s```", stacktraceFormatted);
            }
        }

        builder.setDescription(description);

        return builder.build();
    }

    protected Color getColor(LogLevel level) {
        if (LogLevel.TRACE.equals(level)) {
            return traceColor;
        }
        if (LogLevel.DEBUG.equals(level)) {
            return debugColor;
        }
        if (LogLevel.INFO.equals(level)) {
            return infoColor;
        }
        if (LogLevel.WARN.equals(level)) {
            return warnColor;
        }
        if (LogLevel.ERROR.equals(level)) {
            return errorColor;
        }
        return fallbackColor;
    }

    protected void add(WebhookEmbed embed) {
        synchronized (embedBuffer) {
            embedBuffer.add(embed);

            if (embedBuffer.size() >= maxBufferSize()) {
                flush();
            }
        }
    }

    @Override
    protected void flush() {
        synchronized (embedBuffer) {
            if (embedBuffer.isEmpty()) {
                return;
            }

            sendStrategy.accept(embedBuffer);
            embedBuffer.clear();
        }
    }

    @Override
    protected abstract void startInternally();

    @Override
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
}
