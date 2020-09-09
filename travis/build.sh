#!/usr/bin/env bash
set -e

echo "Building branch '$TRAVIS_BRANCH'"
./gradlew install spotlessCheck jacocoTestReport coveralls -x javadoc -x integrationTest
