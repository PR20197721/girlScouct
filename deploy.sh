#!/bin/bash

VERSION=$1

# Get the current version if version number is not specified
if [ -z $VERSION ]; then
    VERSION=$(mvn -q -Dexec.executable=echo -Dexec.args='${project.version}' --non-recursive exec:exec)
fi

./common/deploy.sh $VERSION  && sleep 20s

./web/deploy.sh $VERSION && sleep 20s

./gsusa/deploy.sh $VERSION && sleep 20s

./vtk/deploy.sh $VERSION
