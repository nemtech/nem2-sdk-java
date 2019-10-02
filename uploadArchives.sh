#!/usr/bin/env bash
set -e

echo "Uploading generated clients jars and javadocs"
./gradlew -b ./openapi-generator/build.gradle uploadArchives publishGhPages

echo "Uploading sdk jars and javadocs"
./gradlew uploadArchives publishGhPages
