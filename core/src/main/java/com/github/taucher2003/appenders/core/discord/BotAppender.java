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

package com.github.taucher2003.appenders.core.discord;

import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookMessage;
import com.github.taucher2003.appenders.core.AbstractDiscordAppender;
import com.github.taucher2003.appenders.utils.DiscordRequester;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class BotAppender extends AbstractDiscordAppender {

    private static final int MAX_EMBED_PER_MESSAGE = 10;
    private final OkHttpClient httpClient = new OkHttpClient.Builder().build();
    private final DiscordRequester discordRequester;

    private String token;
    private long channelId;

    public BotAppender() {
        super.sendStrategy = this::doSend;
        this.discordRequester = new DiscordRequester(SELF_IGNORE_MARKER, httpClient);
    }

    private void doSend(Collection<WebhookEmbed> embeds) {
        WebhookMessage webhookMessage = WebhookMessage.embeds(embeds);
        Request request = buildRequest("https://discord.com/api/channels/" + channelId + "/messages").post(webhookMessage.getBody()).build();
        try {
            discordRequester.request(request).get(10, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException exception) {
            LOGGER.error(SELF_IGNORE_MARKER, "Failed to execute bot message", exception);
        }
    }

    private Request.Builder buildRequest(String url) {
        return new Request.Builder()
                .url(url)
                .header("Authorization", "Bot " + token);
    }

    @Override
    protected void startInternally() {
        if (channelId == 0) {
            stop();
            throw new IllegalArgumentException("No channel set");
        }

        Request request = buildRequest("https://discord.com/api/gateway/bot").get().build();
        Call call = httpClient.newCall(request);
        try (Response response = call.execute()) {
            if (response.isSuccessful() || response.code() == 429) {
                return;
            }
            stop();
            throw new IllegalArgumentException("The provided token is invalid");
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to validate token", e);
        }
    }

    @Override
    protected int maxBufferSize() {
        return MAX_EMBED_PER_MESSAGE;
    }

    @Override
    protected int maxTextSize() {
        return 2048;
    }

    @Override
    protected void stopInternally() {
        discordRequester.shutdown();
    }

    // ---- Setters

    public void setToken(String token) {
        this.token = token;
    }

    public void setChannelId(String channelId) {
        this.channelId = Long.parseLong(channelId);
    }
}
