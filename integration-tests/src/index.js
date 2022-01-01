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

module.exports = (test, jestFn) => {
    const express = require("express");
    const app = express();
    app.use(express.json())

    switch (test) {
        case "DiscordBot":
            const handler = require("./routes/DiscordBot");
            app.use(handler(jestFn));
            break;
        default:
            throw new Error("Unsupported test");
    }

    return new Promise((resolve) => {
        const server = app.listen(6000, '127.0.0.1', () => resolve(server));
    });
};
