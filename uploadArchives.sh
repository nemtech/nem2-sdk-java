#!/usr/bin/env bash
set -e

#echo "Uploading generated clients jars and javadocs"
#./gradlew -b ./openapi-generator/build.gradle uploadArchives gitPublishPush

echo "Uploading sdk jars and javadocs"
./gradlew uploadArchives gitPublishPush
