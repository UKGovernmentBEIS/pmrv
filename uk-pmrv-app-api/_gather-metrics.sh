#!/bin/bash

mvn clean install -P instrument
mvn sonar:sonar
