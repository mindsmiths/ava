#!/bin/bash

if [ -d node_modules ];
then
  PACKAGE_CHANGED=$(find package.json -newer node_modules)
  if [ -z "$PACKAGE_CHANGED" ];
  then
      echo "Requirements up-to-date"
  else
      npm install
  fi
else
  npm install
fi

if [ -d dist ];
then
  NEWER_FILES=$(find package.json babel.config.js vue.config.js src public -newer dist)
  if [ -z "$NEWER_FILES" ];
  then
      echo "No changes"
  else
      npm run build
  fi
else
  npm run build
fi