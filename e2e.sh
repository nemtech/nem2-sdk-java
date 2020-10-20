#!/usr/bin/env bash
set -e


if [ "$TRAVIS" = "true" ]; then
    echo "Installing node"
    nvm install v12.18.3
    npm install -g symbol-bootstrap@alpha
fi
source bootstrap-start.sh -d
gradle integrationTest
source bootstrap-stop.sh
