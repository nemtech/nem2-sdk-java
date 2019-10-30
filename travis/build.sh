#!/usr/bin/env bash
set -e

echo "Current branch is: '$TRAVIS_BRANCH'. Release branch is '$RELEASE_BRANCH'"

if [ "$TRAVIS_BRANCH" != "" ] && [ "$TRAVIS_BRANCH" = "$RELEASE_BRANCH" ]
then
  echo "Currently releasing in branch '$TRAVIS_BRANCH'. Normal build is ignored"
else
  echo "Building branch '$TRAVIS_BRANCH'"
  ./gradlew install jacocoTestReport coveralls -x javadoc
fi