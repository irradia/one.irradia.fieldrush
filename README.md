one.irradia.fieldrush
===

[![Build Status](https://img.shields.io/travis/irradia/one.irradia.fieldrush.svg?style=flat-square)](https://travis-ci.org/irradia/one.irradia.fieldrush)
[![Maven Central](https://img.shields.io/maven-central/v/one.irradia.fieldrush/one.irradia.fieldrush.api.svg?style=flat-square)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22one.irradia.fieldrush%22)
[![Maven Central (snapshot)](https://img.shields.io/nexus/s/https/oss.sonatype.org/one.irradia.fieldrush/one.irradia.fieldrush.api.svg?style=flat-square)](https://oss.sonatype.org/content/repositories/snapshots/one.irradia.fieldrush/)
[![Codacy Badge](https://img.shields.io/codacy/grade/a3e668a39b864af3ade820e5e637778b.svg?style=flat-square)](https://www.codacy.com/app/github_79/one.irradia.fieldrush?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=irradia/one.irradia.fieldrush&amp;utm_campaign=Badge_Grade)
[![Codecov](https://img.shields.io/codecov/c/github/irradia/one.irradia.fieldrush.svg?style=flat-square)](https://codecov.io/gh/irradia/one.irradia.fieldrush)
[![Gitter](https://badges.gitter.im/irradia-org/community.svg)](https://gitter.im/irradia-org/community?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)

![fieldrush](./src/site/resources/fieldrush.jpg?raw=true)

## Features

* High-performance, low-allocation, type-safe, recovering stream parser for constructing data as it is parsed
* Uses the [Jackson](https://github.com/FasterXML/jackson) stream parser internally for performance and correctness
* Declarative, functional API for the correct construction/validation of data values during parsing
* ISC license
* High coverage automated test suite

## Building

Install the Android SDK.

```
$ ./gradlew clean assembleDebug test
```

If the above fails, it's a bug. Report it!

## Using

Use the following Maven or Gradle dependencies, replacing `${LATEST_VERSION_HERE}` with
whatever is the latest version published to Maven Central:

```
<!-- API -->
<dependency>
  <groupId>one.irradia.fieldrush</groupId>
  <artifactId>one.irradia.fieldrush.api</artifactId>
  <version>${LATEST_VERSION_HERE}</version>
</dependency>

<!-- Default implementation -->
<dependency>
  <groupId>one.irradia.fieldrush</groupId>
  <artifactId>one.irradia.fieldrush.vanilla</artifactId>
  <version>${LATEST_VERSION_HERE}</version>
</dependency>
```

```
repositories {
  mavenCentral()
}

implementation "one.irradia.fieldrush:one.irradia.fieldrush.api:${LATEST_VERSION_HERE}"
implementation "one.irradia.fieldrush:one.irradia.fieldrush.vanilla:${LATEST_VERSION_HERE}"
```

Library code is encouraged to depend only upon the API package in order to give consumers
the freedom to use other implementations of the API if desired.

## Modules

|Module|Description|
|------|-----------|
| [one.irradia.fieldrush.api](https://github.com/irradia/one.irradia.fieldrush/tree/develop/one.irradia.fieldrush.api) | Core API
| [one.irradia.fieldrush.tests.device](https://github.com/irradia/one.irradia.fieldrush/tree/develop/one.irradia.fieldrush.tests.device) | Unit tests that execute on real or emulated devices
| [one.irradia.fieldrush.tests](https://github.com/irradia/one.irradia.fieldrush/tree/develop/one.irradia.fieldrush.tests) | Unit tests that can execute without needing a real or emulated device
| [one.irradia.fieldrush.vanilla](https://github.com/irradia/one.irradia.fieldrush/tree/develop/one.irradia.fieldrush.vanilla) | Vanilla implementation

## Publishing Releases

Releases are published to Maven Central with the following invocation:

```
$ ./gradlew clean assembleDebug publish closeAndReleaseRepository
```

Consult the documentation for the [Gradle Signing plugin](https://docs.gradle.org/current/userguide/signing_plugin.html)
and the [Gradle Nexus staging plugin](https://github.com/Codearte/gradle-nexus-staging-plugin/) for
details on what needs to go into your `~/.gradle/gradle.properties` file to do the appropriate
PGP signing of artifacts and uploads to Maven Central.

## Semantic Versioning

All [irradia.one](https://www.irradia.one) packages obey [Semantic Versioning](https://www.semver.org)
once they reach version `1.0.0`.
