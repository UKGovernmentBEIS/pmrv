#!/bin/bash

prod=$1

if [ "$prod" == 'prod' ];
then
  mvn clean install -P prod
else
  mvn clean install -Dmaven.test.skip=true
fi
