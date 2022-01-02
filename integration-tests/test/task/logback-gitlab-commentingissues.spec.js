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
describe("GitLab Commenting Issue Appender for Logback", () => {
    // 3 times = drops all events without throwable
    // 6 times = fetches issues before creating issue
    it("Calls Rest API 6 times", async () => {
        const mock = jest.fn()
        await execute("GitLab-CommentingIssue", mock, logback);
        expect(mock).toBeCalledTimes(6);
    }, 60000);

    it("Rest API path contains configured repository", async () => {
        const mock = jest.fn()
        await execute("GitLab-CommentingIssue", mock, logback);
        // noinspection JSUnresolvedVariable
        expect(mock.mock.calls[0][0].params.projectId).toEqual("1");
    }, 60000);

    it("Calls Rest API with correct headers", async () => {
        const mock = jest.fn()
        await execute("GitLab-CommentingIssue", mock, logback);
        expect(mock.mock.calls[0][0].headers['private-token']).toEqual("ACCESS_TOKEN");
        expect(mock.mock.calls[0][0].headers.accept).toEqual("application/json");
    }, 60000);

    it("Fetches Issues before creating issue", async () => {
        const mock = jest.fn();
        await execute("GitLab-CommentingIssue", mock, logback);
        expect(mock.mock.calls[0][1]).toEqual("Fetched Issues");
    }, 60000);

    it("Creates comments for existing issue", async () => {
        const mock = jest.fn();
        await execute("GitLab-CommentingIssue", mock, logback);
        expect(mock.mock.calls[1][1]).toEqual("Commented on Issue");
        expect(mock.mock.calls[3][1]).toEqual("Commented on Issue");
    }, 60000);

    it("Creates issue for non existing issue", async () => {
        const mock = jest.fn();
        await execute("GitLab-CommentingIssue", mock, logback);
        expect(mock.mock.calls[5][1]).toEqual("Created Issue");
    }, 60000);

    it("Calls Rest API with valid body", async () => {
        const mock = jest.fn();
        await execute("GitLab-CommentingIssue", mock, logback);
        expect(mock.mock.calls[1][0].body).toEqual({
            body: expect.any(String),
            confidential: false
        })
    }, 60000);
})