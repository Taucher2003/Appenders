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

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.AllowedMentions;
import club.minnced.discord.webhook.send.WebhookEmbed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class WebhookAppender extends AbstractDiscordAppender {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebhookAppender.class);

    private static final int MAX_EMBED_PER_MESSAGE = 10;

    private String url;
    private long threadId;
    private WebhookClient webhookClient;

    public WebhookAppender() {
        super.sendStrategy = this::doSend;
    }

    private void doSend(Collection<WebhookEmbed> embeds) {
        try {
            webhookClient.send(embeds).get(10, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException exception) {
            LOGGER.error(SELF_IGNORE_MARKER, "Failed to execute webhook", exception);
        }
    }

    @Override
    protected void startInternally() {
        this.webhookClient = new WebhookClientBuilder(url)
                .setAllowedMentions(AllowedMentions.none())
                .setWait(true)
                .setThreadId(threadId)
                .build();
    }

    @Override
    protected int maxBufferSize() {
        return MAX_EMBED_PER_MESSAGE;
    }

    @Override
    protected int maxTextSize() {
        return 2000;
    }

    @Override
    protected void stopInternally() {
        webhookClient.close();
    }

    // ---- Setters

    public void setUrl(String url) {
        this.url = url;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }
}
