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

const {execute, logback} = require("./runner");
describe("GitHub Commenting Issue Appender for Logback", () => {
    // 3 times = drops all events without throwable
    // 6 times = fetches issues before creating issue
    it("Calls Rest API 6 times", async () => {
        const mock = jest.fn()
        await execute("GitHub-CommentingIssue", mock, logback);
        expect(mock).toBeCalledTimes(6);
    }, 60000);

    it("Rest API path contains configured repository", async () => {
        const mock = jest.fn()
        await execute("GitHub-CommentingIssue", mock, logback);
        // noinspection JSUnresolvedVariable
        expect(mock.mock.calls[0][0].params.repoOwner).toEqual("Taucher2003");
        // noinspection JSUnresolvedVariable
        expect(mock.mock.calls[0][0].params.repoName).toEqual("Appenders");
    }, 60000);

    it("Calls Rest API with correct headers", async () => {
        const mock = jest.fn()
        await execute("GitHub-CommentingIssue", mock, logback);
        expect(mock.mock.calls[0][0].headers.authorization).toEqual("token ACCESS_TOKEN");
        expect(mock.mock.calls[0][0].headers.accept).toEqual("application/vnd.github.v3+json");
    }, 60000);

    it("Fetches Issues before creating issue", async () => {
        const mock = jest.fn();
        await execute("GitHub-CommentingIssue", mock, logback);
        expect(mock.mock.calls.filter(s => s[1] === "Fetched Issues").length).toEqual(3);
    }, 60000);

    it("Creates comments for existing issue", async () => {
        const mock = jest.fn();
        await execute("GitHub-CommentingIssue", mock, logback);
        expect(mock.mock.calls.filter(s => s[1] === "Commented on Issue").length).toEqual(2);
    }, 60000);

    it("Creates issue for non existing issue", async () => {
        const mock = jest.fn();
        await execute("GitHub-CommentingIssue", mock, logback);
        expect(mock.mock.calls.filter(s => s[1] === "Created Issue").length).toEqual(1);
    }, 60000);

    it("Calls Rest API with valid body", async () => {
        const mock = jest.fn();
        await execute("GitHub-CommentingIssue", mock, logback);
        expect(mock.mock.calls.find(c => c[1] === "Commented on Issue")[0].body).toEqual({
            body: expect.any(String)
        });
    }, 60000);
})