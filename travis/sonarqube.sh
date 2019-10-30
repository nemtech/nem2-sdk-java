#!/usr/bin/env bash
set -e

echo "Running sonarqube"
./gradlew sonarqube -x test
