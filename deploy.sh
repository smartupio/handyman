#!/usr/bin/env bash

if [ "$TRAVIS_BRANCH" = 'master' ] && [ "$TRAVIS_PULL_REQUEST" == 'false' ]; then
  gpg --fast-import codesigning.asc
  mvn deploy -Prelease --settings settings.xml
fi
