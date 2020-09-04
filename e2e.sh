#!/usr/bin/env bash
set -e


if [ "$TRAVIS" = "true" ]; then
    echo "Installing node"
#    nvm install v12.18.3
    npm install -g symbol-bootstrap
fi
source bootstrap-start.sh
gradle integrationTest
source bootstrap-stop.sh
