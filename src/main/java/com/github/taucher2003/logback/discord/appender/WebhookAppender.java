package com.github.taucher2003.logback.discord.appender;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.core.AppenderBase;
import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.AllowedMentions;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.awt.Color;
import java.time.Instant;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class WebhookAppender extends AppenderBase<ILoggingEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebhookAppender.class);
    private static final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newScheduledThreadPool(1);
    private static final int MAX_EMBED_PER_MESSAGE = 10;
    private static final Collection<WebhookAppender> INSTANCES = new CopyOnWriteArrayList<>();
    private static final Marker SELF_IGNORE_MARKER = MarkerFactory.getMarker(WebhookAppender.class.getCanonicalName());

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(WebhookAppender::shutdown));
    }

    private static void shutdown() {
        INSTANCES.forEach(WebhookAppender::stop);
        EXECUTOR_SERVICE.shutdown();
    }

    private String url;
    private int sendingInterval = 5;
    private TimeUnit sendingUnit = TimeUnit.SECONDS;
    private WebhookClient webhookClient;

    private Color errorColor = new Color(0xFF0000);
    private Color warnColor = new Color(0xFFC800);
    private Color infoColor = new Color(0x32CD32);
    private Color debugColor = new Color(0x116DF6);
    private Color traceColor = new Color(0x0117C2);
    private Color fallbackColor = new Color(0x777777);

    private final Collection<WebhookEmbed> embedBuffer = new CopyOnWriteArrayList<>();
    private final Collection<String> markerNames = new CopyOnWriteArrayList<>();

    private ScheduledFuture<?> sendFuture;

    public WebhookAppender() {
        INSTANCES.add(this);
    }

    @Override
    public void start() {
        this.webhookClient = new WebhookClientBuilder(url)
                .setAllowedMentions(AllowedMentions.none())
                .setExecutorService(EXECUTOR_SERVICE)
                .buildJDA();
        this.sendFuture = EXECUTOR_SERVICE.scheduleAtFixedRate(this::send, sendingInterval, sendingInterval, sendingUnit);
        super.start();
    }

    @Override
    public void stop() {
        super.stop();
        this.sendFuture.cancel(false);
        send();
    }

    @Override
    protected void append(ILoggingEvent iLoggingEvent) {
        if(iLoggingEvent.getMarker().contains(SELF_IGNORE_MARKER)) {
            return;
        }
        if(!markerNames.isEmpty() && !markerNames.contains(iLoggingEvent.getMarker().getName())) {
            return;
        }

        WebhookEmbed embed = createEmbed(iLoggingEvent);
        add(embed);
    }

    private WebhookEmbed createEmbed(ILoggingEvent iLoggingEvent) {
        WebhookEmbedBuilder builder = new WebhookEmbedBuilder()
                .setTitle(new WebhookEmbed.EmbedTitle(iLoggingEvent.getLevel().toString(), null))
                .setTimestamp(Instant.ofEpochMilli(iLoggingEvent.getTimeStamp()))
                .setColor(getColor(iLoggingEvent.getLevel()).getRGB())
                .setFooter(new WebhookEmbed.EmbedFooter(iLoggingEvent.getLoggerName() + "@" + iLoggingEvent.getThreadName(), null));

        String description = Utils.shortenWithEndCut(iLoggingEvent.getFormattedMessage(), MessageEmbed.TEXT_MAX_LENGTH);

        IThrowableProxy throwable = iLoggingEvent.getThrowableProxy();
        if (throwable != null) {
            int remainingCharacters = MessageEmbed.TEXT_MAX_LENGTH - description.length();

            remainingCharacters -= "\n\n``````".length();

            if (remainingCharacters > 0) {
                String stacktraceFormatted = Utils.getExceptionStacktrace(throwable, remainingCharacters);
                description += String.format("\n\n```%s```", stacktraceFormatted);
            }
        }

        builder.setDescription(description);

        return builder.build();
    }

    private Color getColor(Level level) {
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

    private void add(WebhookEmbed embed) {
        synchronized (embedBuffer) {
            embedBuffer.add(embed);

            if(embedBuffer.size() >= MAX_EMBED_PER_MESSAGE) {
                send();
            }
        }
    }

    private void send() {
        synchronized (embedBuffer) {
            if (embedBuffer.isEmpty()) {
                return;
            }

            try {
                webhookClient.send(embedBuffer).get(10, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException exception) {
                LOGGER.error(SELF_IGNORE_MARKER, "Failed to execute webhook", exception);
            }
            embedBuffer.clear();
        }
    }

    // ---- Setters

    public void setUrl(String url) {
        this.url = url;
    }

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

    public void setMarker(String marker) {
        markerNames.add(marker);
    }
}
