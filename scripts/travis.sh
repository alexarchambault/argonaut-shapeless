#!/usr/bin/env bash
set -xe

SBT_COMMANDS=("++${TRAVIS_SCALA_VERSION}")

if [[ "$TRAVIS_SCALA_VERSION" == 2.10.* ]]; then
  SBT_COMMANDS+=("test")
else
  SBT_COMMANDS+=("validate")
fi

if [[ "$TRAVIS_PULL_REQUEST" == "false" && "$JAVA_HOME" == "$(jdk_switcher home openjdk8)" && "$TRAVIS_BRANCH" == "master" ]]; then
  SBT_COMMANDS+=("publish")
fi

sbt ${SBT_COMMANDS[@]}
