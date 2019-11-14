#!/usr/bin/env bash
set -e

echo "Building branch '$TRAVIS_BRANCH'"
./gradlew install jacocoTestReport coveralls -x javadoc
