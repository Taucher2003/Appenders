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

package com.github.taucher2003.appenders.it.log4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LogMessageSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogMessageSender.class);

    private LogMessageSender() {
    }

    public static void main(String[] args) {
        LOGGER.info("Test");
        LOGGER.warn("Test");
        LOGGER.error("Test");
        LOGGER.info("Test", new Throwable("Exception"));
        LOGGER.warn("Test", new Throwable("Exception"));
        LOGGER.error("Test", new Throwable("Exception 2"));
        LOGGER.info("Other Test");
        LOGGER.warn("Other Test");
        LOGGER.error("Other Test");
        LOGGER.info("Third Other Test");
        LOGGER.warn("Third Other Test");
        LOGGER.error("Third Other Test");
    }
}
