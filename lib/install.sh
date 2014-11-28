#!/usr/bin/env bash

mvn install:install-file -Dfile=lib/tdgssconfig.jar -DgroupId=com.teradata -DartifactId=tdgssconfig -Dversion=15.00 -Dpackaging=jar