/*
 *
 *  Copyright 2021 Niklas van Schrick and the contributors of the Appenders Project
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

package com.github.taucher2003.appenders.logback.github;

import com.github.taucher2003.appenders.core.github.IssueAppender;

public class LogbackIssueAppender extends AbstractLogbackGithubAppender<IssueAppender> {

    public LogbackIssueAppender() {
        super(new IssueAppender());
    }

    public void setLabel(String label) {
        delegate.setLabel(label);
    }
}
