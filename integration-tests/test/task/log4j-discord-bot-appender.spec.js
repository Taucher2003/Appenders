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

const {execute, log4j} = require("./runner");
describe("Bot Appender for Log4J", () => {
    it("Calls Rest API 2 times", async () => {
        const mock = jest.fn()
        await execute("DiscordBot", mock, log4j);
        expect(mock).toBeCalledTimes(2);
    }, 60000);

    it("Rest API path contains configured channel id", async () => {
        const mock = jest.fn()
        await execute("DiscordBot", mock, log4j);
        expect(mock.mock.calls[0][0].params.id).toEqual("123");
    }, 60000);

    it("Calls Rest API with correct Authorization header", async () => {
        const mock = jest.fn()
        await execute("DiscordBot", mock, log4j);
        expect(mock.mock.calls[0][0].headers.authorization).toEqual("Bot ABC");
    }, 60000);

    it("Calls Rest API with valid body", async () => {
        const mock = jest.fn()
        await execute("DiscordBot", mock, log4j);

        // we use the second call as there are less embeds
        const timestampRegex = /\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}\.\d{3}Z/;
        expect(mock.mock.calls[1][0].body).toEqual({
            allowed_mentions: {parse: ["users", "roles", "everyone"]},
            embeds: [{
                color: 16762880,
                description: "Third Other Test",
                footer: {text: "com.github.taucher2003.appenders.it.log4j.LogMessageSender@main"},
                timestamp: expect.stringMatching(timestampRegex),
                title: "WARN"
            }, {
                color: 16711680,
                description: "Third Other Test",
                footer: {text: "com.github.taucher2003.appenders.it.log4j.LogMessageSender@main"},
                timestamp: expect.stringMatching(timestampRegex),
                title: "ERROR"
            }],
            flags: 0,
            tts: false
        })
    }, 60000);
});