# Appenders

This project provides multiple appenders for logging frameworks. Currently, only logback is supported but more are
planned.

## 🚩 Table of Contents

<ol>
    <li><a href="#-installation">Installation</a></li>
    <li><a href="#-configuration">Configuration</a></li>
    <li><a href="#-contributing">Contributing</a></li>
</ol>

## ⚡ Installation

This project is published with the Maven Central repository.

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
<summary>Logback</summary>

You need to create a new appender in your `logback.xml` configuration. \
As class, you can choose between `com.github.taucher2003.appender.logback.discord.BotAppender`
and `com.github.taucher2003.appender.logback.discord.WebhookAppender`.

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
| sendingInterval | Set the interval which is used to regularly flush the buffer |
| sendingUnit | Set the TimeUnit name for the `sendingInterval`. This is required to be a valid enum constant of `java.util.concurrent.TimeUnit` | 
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
    <appender name="discord-bot" class="com.github.taucher2003.appender.logback.discord.BotAppender">
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

## 🔮 Contributing

Contributions are very welcome. Feel free to open issues, fork the project and create pull requests. Contribution
guidelines maybe follow later. Every contribution is valuable, so just don't be a dick.