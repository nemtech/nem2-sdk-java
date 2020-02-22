# symbol-sdk for Java/Kotlin/Scala
#

[![symbol-sdk-vertx-client](https://maven-badges.herokuapp.com/maven-central/io.nem/symbol-sdk-vertx-client/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.nem/symbol-sdk-vertx-client)
[![symbol-sdk-okhttp-client](https://maven-badges.herokuapp.com/maven-central/io.nem/symbol-sdk-okhttp-client/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.nem/symbol-sdk-okhttp-client)
[![Build Status](https://api.travis-ci.org/nemtech/symbol-sdk-java.svg?branch=master)](https://travis-ci.org/nemtech/symbol-sdk-java)
[![Coverage Status](https://coveralls.io/repos/github/nemtech/symbol-sdk-java/badge.svg?branch=master)](https://coveralls.io/github/nemtech/symbol-sdk-java?branch=master)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)


The official symbol-sdk for Java, Kotlin and Scala to work with the SYMBOL (a.k.a Catapult).

## Requirements

- Java 8
- Java 9

## Usage

Each SDK user can depend on the best library for its need (example, ``symbol-sdk-vertx-client`` for server developers or ``symbol-sdk-okhttp-client`` for android developers).

### Maven

```xml
<dependency>
    <groupId>io.nem</groupId>
    <artifactId>symbol-sdk-vertx-client</artifactId>
    <version>0.17.0</version>
</dependency>
```

OR

```xml
<dependency>
    <groupId>io.nem</groupId>
    <artifactId>symbol-sdk-okhttp-client</artifactId>
    <version>0.17.0</version>
</dependency>
```


### Gradle

```compile 'io.nem:symbol-sdk-vertx-client:0.17.0```

OR

```compile 'io.nem:symbol-sdk-okhttp-client:0.17.0```


### SBT

```libraryDependencies += "io.nem" % "symbol-sdk-vertx-client" % "0.17.0"```

OR

```libraryDependencies += "io.nem" % "symbol-sdk-okhttp-client" % "0.17.0"```


## Documentation and Getting Started

Get started and learn more about symbol-sdk-java, check the [official documentation][docs].

Check SDK reference [here][sdk-ref]

### Open API Generated Clients

The SDK libs depend on Open API 3 generated clients. The clients Jars are automatically generated and deployed into Maven central by the [symbol-openapi-generator][symbol-openapi-generator] project. 

If you want to change or tune the generated libraries, you would need to clone/fork [symbol-openapi-generator][symbol-openapi-generator] repository.

## Modules

The SDK is composed of multiple sub-modules/folders:

- **sdk-core:** This module includes the model objects, interfaces and common utility classes. It is Vertx, ok-http, gson, etc agnostic. Clients won't depend on this jar directly, they will depend on one of the implementations below.
- **sdk-vertx-client:** The symbol-sdk-java Implementation that uses Vertx and generated `api-vertx-client` lib and dtos. A client may depend on this SDK implementation if Vertx is the selected implementation (e.g. server users).
- **sdk-okhttp-client:** The symbol-sdk-java Implementation that uses OkHttp and the generated `api-okhttp-gson-client`. A client may depend on this SDK implementation if OkHttp is the selected implementation (e.g. android users).
- **integration-tests:** This module is in charge of running integration tests against all implementations. The integration tests exercise how the implementation work against a given catapult server.


## symbol-sdk Releases

The release notes for the symbol-sdk can be found [here](CHANGELOG.md).

## Contributing

This project is developed and maintained by NEM Foundation. Contributions are welcome and appreciated. You can find [symbol-sdk on GitHub][self];
Feel free to start an issue or create a pull request. Check [CONTRIBUTING](CONTRIBUTING.md) before start.

## Getting help

We use GitHub issues for tracking bugs and have limited bandwidth to address them.
Please, use the following available resources to get help:

- [symbol-cli documentation][docs]
- If you found a bug, [open a new issue][issues]

## License

Copyright (c) 2018 NEM
Licensed under the [Apache License 2.0](LICENSE)

[self]: https://github.com/nemtech/symbol-sdk-java
[docs]: http://nemtech.github.io/getting-started/setup-workstation.html
[issues]: https://github.com/nemtech/symbol-sdk-java/issues
[sdk-ref]: http://nemtech.github.io/symbol-sdk-java/javadoc/0.17.0/
[symbol-openapi-generator]: https://github.com/nemtech/symbol-openapi-generator
