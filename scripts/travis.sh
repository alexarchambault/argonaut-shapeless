#!/usr/bin/env bash
set -xe

SBT_COMMANDS=("++${TRAVIS_SCALA_VERSION}!")

if [[ "$NATIVE" = 1 ]]; then
  SBT_COMMANDS+=("native/test")
elif [[ "$TRAVIS_SCALA_VERSION" == 2.10.* ]]; then
  SBT_COMMANDS+=("test")
else
  SBT_COMMANDS+=("validate")
fi

if [[ "$TRAVIS_PULL_REQUEST" == "false" && "$JAVA_HOME" == "$(jdk_switcher home oraclejdk8)" && "$TRAVIS_BRANCH" == "master" ]]; then
  if [[ "$NATIVE" = 1 ]]; then
    SBT_COMMANDS+=("native/publish")
  else
    SBT_COMMANDS+=("publish")
  fi
fi

sbt ${SBT_COMMANDS[@]}
