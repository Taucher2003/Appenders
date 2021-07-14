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

package com.github.taucher2003.appenders.logback.discord;

import com.github.taucher2003.appenders.core.discord.WebhookAppender;

public class LogbackWebhookAppender extends AbstractLogbackDiscordAppender<WebhookAppender> {

    public LogbackWebhookAppender() {
        super(new WebhookAppender());
    }

    @Override
    public void start() {
        super.start();
        delegate.start();
    }

    @Override
    public void stop() {
        delegate.stop();
        super.stop();
    }

    // ---- Setters

    public void setUrl(String url) {
        delegate.setUrl(url);
    }
}
