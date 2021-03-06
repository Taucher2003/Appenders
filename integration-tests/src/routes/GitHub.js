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

module.exports = (jestFn) => {
    const router = require('express').Router();
    router.post('/repos/:repoOwner/:repoName/issues', (req, res) => {
        jestFn(req, "Created Issue");
        res.status(200).end();
    });
    router.get('/repos/:repoOwner/:repoName/issues', (req, res) => {
        jestFn(req, "Fetched Issues");
        res.status(200).send([
            {
                title: "java.lang.Throwable: Exception",
                number: 2
            }
        ])
    });
    router.post('/repos/:repoOwner/:repoName/issues/:issueId/comments', (req, res) => {
        jestFn(req, "Commented on Issue");
        res.status(200).end();
    })
    return router;
}