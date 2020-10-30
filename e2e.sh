#!/usr/bin/env bash
set -e

if [ "$TRAVIS" = "true" ]; then
    echo "Installing node"
    nvm install v12.18.3
    npm install -g symbol-bootstrap@0.2.1-alpha-202010281951
fi
./gradlew build
source bootstrap-start.sh -d
./gradlew integrationTest
source bootstrap-stop.sh
