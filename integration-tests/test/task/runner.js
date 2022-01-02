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

const {spawn} = require("child_process");
const startMockServer = require("../../src/index");
const fs = require("fs");

module.exports.execute = (test, jestFn, {framework, configName}) => {
    // security against arbitrary code execution in spawn() below
    switch (framework) {
        case "log4j":
        case "logback":
            break;
        default:
            throw new Error("Unsupported Framework");
    }

    copyConfig(framework, configName, test);

    return startMockServer(test, jestFn).then((server) => {
        const childProcess = spawn("java", [`com.github.taucher2003.appenders.it.${framework}.LogMessageSender`], {
            cwd: `${framework}-it/target`,
            timeout: 10000
        });

        childProcess.stdout.on('data', data => console.info(`${data}`));
        childProcess.stderr.on('data', data => console.error(`${data}`));

        return new Promise((resolve) => {
            childProcess.on('close', () => {
                server.close();
                removeConfig(framework, configName);
                setTimeout(resolve, 5000);
            })
        })
    })
}

function copyConfig(framework, configName, test) {
    fs.copyFileSync(`test/config/${framework}-${test}.xml`, `${framework}-it/target/${configName}.xml`)
}

function removeConfig(framework, configName) {
    fs.rmSync(`${framework}-it/target/${configName}.xml`);
}

module.exports.logback = {framework: "logback", configName: "logback"};
module.exports.log4j = {framework: "log4j", configName: "log4j2"};
