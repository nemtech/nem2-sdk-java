# Symbol SDK for Java/Kotlin/Scala

[![symbol-sdk-vertx-client](https://maven-badges.herokuapp.com/maven-central/io.nem/symbol-sdk-vertx-client/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.nem/symbol-sdk-vertx-client)
[![symbol-sdk-okhttp-client](https://maven-badges.herokuapp.com/maven-central/io.nem/symbol-sdk-okhttp-client/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.nem/symbol-sdk-okhttp-client)
[![Build Status](https://api.travis-ci.com/nemtech/symbol-sdk-java.svg?branch=main)](https://travis-ci.com/nemtech/symbol-sdk-java)
[![Coverage Status](https://coveralls.io/repos/github/nemtech/symbol-sdk-java/badge.svg?branch=main)](https://coveralls.io/github/nemtech/symbol-sdk-java?branch=main)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

The Symbol SDK for Java, Kotlin and Scala.

## Requirements

- Java 8
- Java 9

## Installation

Each SDK user can depend on the best library for its need (example, ``symbol-sdk-vertx-client`` for server developers or ``symbol-sdk-okhttp-client`` for android developers).

### Maven

```xml
<dependency>
    <groupId>io.nem</groupId>
    <artifactId>symbol-sdk-vertx-client</artifactId>
    <version>0.23.0</version>
</dependency>
```

OR

```xml
<dependency>
    <groupId>io.nem</groupId>
    <artifactId>symbol-sdk-okhttp-client</artifactId>
    <version>0.23.0</version>
</dependency>
```

### Gradle

```compile 'io.nem:symbol-sdk-vertx-client:0.23.0```

OR

```compile 'io.nem:symbol-sdk-okhttp-client:0.23.0```

### SBT

```libraryDependencies += "io.nem" % "symbol-sdk-vertx-client" % "0.23.0"```

OR

```libraryDependencies += "io.nem" % "symbol-sdk-okhttp-client" % "0.23.0"```

## Usage

Surf the [documentation][docs] to get started into Symbol development.
You will find self-paced guides and useful code snippets using the Java SDK.

To get the full description of the available classes and their functions, check the [SDK reference][sdk-ref].

## Pacakge Organization

The SDK is composed of multiple sub-modules/folders:

| Module  | Description |
|---------|-------------|
|sdk-core | This module includes the model objects, interfaces and common utility classes. It is Vertx, ok-http, gson, etc agnostic. Clients won't depend on this jar directly, they will depend on one of the implementations below. |
|sdk-vertx-client| The symbol-sdk-java Implementation that uses Vertx and generated `symbol-openapi-vertx-client` lib and dtos. A client may depend on this SDK implementation if Vertx is the selected implementation (e.g. server users).|
|sdk-okhttp-client| The symbol-sdk-java Implementation that uses OkHttp and the generated `symbol-openapi-okhttp-gson-client`. A client may depend on this SDK implementation if OkHttp is the selected implementation (e.g. android users). |
|integration-tests|This module is in charge of running integration tests against all implementations. The integration tests exercise how the implementation work against a given catapult server.|

### Open API Generated Clients

The SDK libs depend on Open API 3 generated clients. The clients Jars are automatically generated and deployed into Maven central by the [symbol-openapi-generator][symbol-openapi-generator] project. 

If you want to change or tune the generated libraries, you would need to clone/fork [symbol-openapi-generator][symbol-openapi-generator] repository.

## Getting help

Use the following available resources to get help:

- [Symbol Documentation][docs]
- [Symbol SDK Java Reference][sdk-ref]
- Join the community [slack group (#sig-api)][slack] 
- If you found a bug, [open a new issue][issues]

## Contributing

Contributions are welcome and appreciated. 
Check [CONTRIBUTING](CONTRIBUTING.md) for information on how to contribute.

You can also find useful notes for developers under our documentation [guidelines][guidelines] section.

## License

Copyright (c) 2018-present NEM
Licensed under the [Apache License 2.0](LICENSE)

[self]: https://github.com/nemtech/symbol-sdk-java
[docs]: http://nemtech.github.io/getting-started/setup-workstation.html
[issues]: https://github.com/nemtech/symbol-sdk-java/issues
[sdk-ref]: https://nemtech.github.io/references/java-sdk.html
[symbol-openapi-generator]: https://github.com/nemtech/symbol-openapi-generator
[guidelines]: https://nemtech.github.io/contribute/contributing.html#sdk
[slack]: https://join.slack.com/t/nem2/shared_invite/enQtMzY4MDc2NTg0ODgyLWZmZWRiMjViYTVhZjEzOTA0MzUyMTA1NTA5OWQ0MWUzNTA4NjM5OTJhOGViOTBhNjkxYWVhMWRiZDRkOTE0YmU
