# Appenders

This project provides multiple appenders for logging frameworks. Currently, only Logback and Log4J are supported but
more are planned.

## 🚩 Table of Contents

<ol>
    <li><a href="#-appender-list">Appender List</a></li>
    <li><a href="#-installation">Installation</a></li>
    <li><a href="#-configuration">Configuration</a></li>
    <li><a href="#-known-bugs">Known Bugs</a></li>
    <li><a href="#-contributing">Contributing</a></li>
</ol>

## 📂 Appender List

| Appender name | Description |
|---------------|-------------|
| DiscordWebhook | Sends log entries via Webhook to a Discord channel |
| DiscordBot | Sends log entries via Bot account to a Discord channel |
| GithubIssue | Creates an issue for exceptions on a Github Repository |
| GithubCommentingIssue | Creates an issue for exceptions on a Github Repository, but tries to prevent duplicated issues |
| GitlabIssue | Creates an issue for exceptions on a Gitlab Repository |
| GitlabCommentingIssue | Creates an issue for exceptions on a Gitlab Repository, but tries to prevent duplicated issues |

## ⚡ Installation

The releases of the Appenders Project are published in the Central repository. \
If you prefer pinning your version to a branch or commit instead of version numbers, these are deployed in GitLab.

<details>
<summary>Branch, Commit & Snapshot Versions</summary>

If you want to use branch and commit versions, you need to add new repository to your build configuration. \
This only provides information about the repository and the version information. For the actual dependency, please look below at the framework details.

### Maven

```xml
<repository>
    <id>appenders-gitlab</id>
    <url>https://gitlab.com/api/v4/groups/12234336/-/packages/maven</url>
</repository>
```

### Gradle

```groovy
repositories {
    maven {
        url "https://gitlab.com/api/v4/groups/12234336/-/packages/maven"
    }
}
```

### Snapshot Versions

Snapshot versions get deployed on each push to `development`. \
[Click here](https://gitlab.com/taucher2003-group/appenders/-/packages) to see a list of available versions.

### Branch versions

Branch versions are deployed every hour if the deployed version is out of date. \
They have set the version to `<BRANCH_SLUG>-SNAPSHOT`. \
[Click here](https://gitlab.com/taucher2003-group/appenders-branch/-/packages) to see a list of available versions.

### Commit versions

Commit versions have set the version to the full commit sha. \
[Click here](https://gitlab.com/taucher2003-group/appenders-commit/-/packages) to see a list of available versions.

</details>

<details>
<summary>Log4J</summary>

### Maven

```xml
<dependency>
    <groupId>com.github.taucher2003.appenders</groupId>
    <artifactId>log4j</artifactId>
    <version>VERSION</version>
    <scope>compile</scope>
</dependency>
```

### Gradle

```groovy
dependencies {
    implementation 'com.github.taucher2003.appenders:log4j:VERSION'
}
```

</details>

<details>
<summary>Logback</summary>

### Maven

```xml
<dependency>
    <groupId>com.github.taucher2003.appenders</groupId>
    <artifactId>logback</artifactId>
    <version>VERSION</version>
    <scope>compile</scope>
</dependency>
```

### Gradle

```groovy
dependencies {
    implementation 'com.github.taucher2003.appenders:logback:VERSION'
}
```

</details>

<details>
<summary>All Implementations</summary>

### Maven

```xml
<dependency>
    <groupId>com.github.taucher2003.appenders</groupId>
    <artifactId>all</artifactId>
    <version>VERSION</version>
    <scope>compile</scope>
</dependency>
```

### Gradle

```groovy
dependencies {
    implementation 'com.github.taucher2003.appenders:all:VERSION'
}
```

</details>

## ⚙ Configuration

<details>
<summary>Log4J Discord</summary>

You need to create a new appender in your `log4j2.xml` configuration. \
As plugin, you can choose between `DiscordBot` and `DiscordWebhook`.

The DiscordBot plugin requires the two settings `token` and `channelId`. \
The DiscordWebhook plugin however, just requires the `url` setting.

#### Example Webhook and Bot Configuration

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="[...]" shutdownHook="[...]" packages="[...]">
    <Appenders>
        [...] existing appenders

        <DiscordWebhook name="DiscordWebhook"
                        url="[your webhook url]">
            <filters>
                <MarkerFilter marker="discord-webhook" onMatch="ACCEPT" onMismatch="DENY"/>
            </filters>
        </DiscordWebhook>

        <DiscordBot name="DiscordBot"
                    token="[your bot token]"
                    channelId="[your channel id]">
            <MarkerFilter marker="discord-bot" onMatch="ACCEPT" onMismatch="DENY"/>
        </DiscordBot>

    </Appenders>
    <Loggers>
        <Root level="[...]">
            [...] existing loggers

            <AppenderRef ref="DiscordWebhook"/>
            <AppenderRef ref="DiscordBot"/>
        </Root>
    </Loggers>
</Configuration>
```

</details>

<details>
<summary>Log4J Github</summary>

You need to create a new appender in your `log4j2.xml` configuration. \
As plugin, you can choose between `GithubIssue` and `GithubCommentingIssue`.

Both of them require the settings `baseUrl`, `repositoryOwner`, `repositoryName` and `accessToken`. \
`baseUrl` is the base url of the Github API. For github.com users, this would be `https://api.github.com`.
`repositoryOwner` defines the name of the account, which owns the repository. That is either your username or
organization name.
`repositoryName` sets the name of the repository itself, which will be used to create the issues. The `accessToken` will
be used for authorization. The issues will be created with the account, where the access token belongs to.

This appender will only log events which have a throwable attached. All log events without a throwable will be dropped
by this appender. The log level does not matter.

#### Example Configuration with both appenders

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="[...]" shutdownHook="[...]" packages="[...]">
    <Appenders>
        [...] existing appenders

        <GithubIssue name="GithubIssue"
                        baseUrl="https://api.github.com"
                        repositoryOwner="Taucher2003"
                        repositoryName="Appenders"
                        accessToken="[your access token]"/>

        <GithubCommentingIssue name="GithubCommentingIssue"
                     baseUrl="https://api.github.com"
                     repositoryOwner="Taucher2003"
                     repositoryName="Appenders"
                     accessToken="[your access token]"/>

    </Appenders>
    <Loggers>
        <Root level="[...]">
            [...] existing loggers

            <AppenderRef ref="GithubIssue"/>
            <AppenderRef ref="GithubCommentingIssue"/>
        </Root>
    </Loggers>
</Configuration>
```

</details>

<details>
<summary>Log4J Gitlab</summary>

You need to create a new appender in your `log4j2.xml` configuration. \
As plugin, you can choose between `GitlabIssue` and `GitlabCommentingIssue`.

Both of them require the settings `baseUrl`, `repositoryId`, `accessToken` and `confidential`. \
`baseUrl` is the base url of the GitLab instance. For gitlab.com users, this would be `https://gitlab.com`.
`repositoryId` defines the id of the repository, which will be used to create the issues. The `accessToken` will be used
for authorization. The issues will be created with the account, where the access token belongs to. If `confidential` is
set to true, the issues will be created as confidential issue, so they are hidden from guest users.

This appender will only log events which have a throwable attached. All log events without a throwable will be dropped
by this appender. The log level does not matter.

#### Example Configuration with both appenders

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="[...]" shutdownHook="[...]" packages="[...]">
    <Appenders>
        [...] existing appenders

        <GitlabIssue name="GitlabIssue"
                     baseUrl="https://gitlab.com"
                     repositoryId="1"
                     accessToken="[your access token]"
                     confidential="true"/>

        <GitlabCommentingIssue name="GitlabCommentingIssue"
                               baseUrl="https://gitlab.com"
                               repositoryId="1"
                               accessToken="[your access token]"/>

    </Appenders>
    <Loggers>
        <Root level="[...]">
            [...] existing loggers

            <AppenderRef ref="GitlabIssue"/>
            <AppenderRef ref="GitlabCommentingIssue"/>
        </Root>
    </Loggers>
</Configuration>
```

</details>

<details>
<summary>Logback Discord</summary>

You need to create a new appender in your `logback.xml` configuration. \
As class, you can choose between `com.github.taucher2003.appenders.logback.discord.LogbackBotAppender`
and `com.github.taucher2003.appenders.logback.discord.LogbackWebhookAppender`.

The BotAppender requires the two settings `token` and `channelId`. \
The WebhookAppender however, just requires the `url` setting.

Both of them allow shared settings. These are not required and have reasonable default settings.

| Setting name | What it does |
|--------------|--------------|
| errorColor | Set the embed color of the error level |
| warnColor | Set the embed color of the warn level |
| infoColor | Set the embed color of the info level |
| debugColor | Set the embed color of the debug level |
| traceColor | Set the embed color of the trace level |
| fallbackColor | Set the embed color for unknown levels |
| flushInterval | Set the interval which is used to regularly flush the buffer |
| flushUnit | Set the TimeUnit name for the `sendingInterval`. This is required to be a valid enum constant of `java.util.concurrent.TimeUnit` | 
| marker | Add a marker to the list of allowed markers for this logger |
| ignoredMarker | Add a marker, which is ignored |
| level | Add a level which should be logged |

If no values have been set for `marker`, all log events will be handled by the logger. If at least one `marker` has been
set, only log events with a marker named like one in the list will be handled and log events without or with other
markers will be dropped by this logger.

Same applies to `level` and `ignoredMarker`. \
`level` is used to filter for logging levels and `ignoredMarker` will set markers, which will be dropped.

#### Example Bot Configuration

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration debug="false">
    [...] existing configuration
    <appender name="discord-bot" class="com.github.taucher2003.appenders.logback.discord.LogbackBotAppender">
        <token>[your bot token]</token>
        <channelId>[your channel id]</channelId>
        <level>ERROR</level> <!-- Restrict the logger to ERROR level -->
        <ignoredMarker>discord-ignored</ignoredMarker> <!-- Ignore all log events with the "discord-ignored" marker -->
    </appender>
    <root level="INFO">
        [...] other existing appenders
        <appender-ref ref="discord-bot"/>
    </root>
</configuration>
```

</details>

<details>
<summary>Logback Github</summary>

You need to create a new appender in your `logback.xml` configuration. \
As class, you can choose between `com.github.taucher2003.appenders.logback.github.LogbackIssueAppender`
and `com.github.taucher2003.appenders.logback.github.LogbackCommentingIssueAppender`.

Both of them require the settings `baseUrl`, `repositoryOwner`, `repositoryName` and `accessToken`. \
`baseUrl` is the base url of the Github API. For github.com users, this would be `https://api.github.com`.
`repositoryOwner` defines the name of the account, which owns the repository. That is either your username or
organization name.
`repositoryName` sets the name of the repository itself, which will be used to create the issues. The `accessToken` will
be used for authorization. The issues will be created with the account, where the access token belongs to.

This appender will only log events which have a throwable attached. All log events without a throwable will be dropped
by this appender.

If no values have been set for `marker`, all log events will be handled by the logger. If at least one `marker` has been
set, only log events with a marker named like one in the list will be handled and log events without or with other
markers will be dropped by this logger. \
Same applies to `level` and `ignoredMarker`. \
`level` is used to filter for logging levels and `ignoredMarker` will set markers, which will be dropped.

#### Example Issue Configuration

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration debug="false">
    [...] existing configuration
    <appender name="github-issues" class="com.github.taucher2003.appenders.logback.github.LogbackIssueAppender">
        <baseUrl>https://api.github.com</baseUrl>
        <repositoryOwner>Taucher2003</repositoryOwner>
        <repositoryName>Appenders</repositoryName>
        <accessToken>[your access token]</accessToken>
    </appender>
    <root level="INFO">
        [...] other existing appenders
        <appender-ref ref="github-issues"/>
    </root>
</configuration>
```

</details>

<details>
<summary>Logback Gitlab</summary>

You need to create a new appender in your `logback.xml` configuration. \
As class, you can choose between `com.github.taucher2003.appenders.logback.gitlab.LogbackIssueAppender`
and `com.github.taucher2003.appenders.logback.gitlab.LogbackCommentingIssueAppender`.

Both of them require the settings `baseUrl`, `repositoryId`, `accessToken` and `confidential`. \
`baseUrl` is the base url of the GitLab instance. For gitlab.com users, this would be `https://gitlab.com`.
`repositoryId` defines the id of the repository, which will be used to create the issues. The `accessToken` will be used
for authorization. The issues will be created with the account, where the access token belongs to. If `confidential` is
set to true, the issues will be created as confidential issue, so they are hidden from guest users.

This appender will only log events which have a throwable attached. All log events without a throwable will be dropped
by this appender.

If no values have been set for `marker`, all log events will be handled by the logger. If at least one `marker` has been
set, only log events with a marker named like one in the list will be handled and log events without or with other
markers will be dropped by this logger. \
Same applies to `level` and `ignoredMarker`. \
`level` is used to filter for logging levels and `ignoredMarker` will set markers, which will be dropped.

#### Example Issue Configuration

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration debug="false">
    [...] existing configuration
    <appender name="gitlab-issues" class="com.github.taucher2003.appenders.logback.gitlab.LogbackIssueAppender">
        <baseUrl>https://gitlab.com</baseUrl>
        <repositoryId>1</repositoryId>
        <accessToken>[your access token]</accessToken>
        <confidential>true</confidential>
    </appender>
    <root level="INFO">
        [...] other existing appenders
        <appender-ref ref="gitlab-issues"/>
    </root>
</configuration>
```

</details>

## 🐛 Known Bugs

* Appenders prevents shutdown, requires explicit `System.exit(code)` to trigger the shutdown hook (#22)

## 🔮 Contributing

Contributions are very welcome. Feel free to open issues, fork the project and create pull requests. Contribution
guidelines maybe follow later. Every contribution is valuable, so just don't be a dick.