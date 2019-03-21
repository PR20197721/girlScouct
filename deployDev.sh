#!/bin/bash

VERSION=$1

# Get the current version if version number is not specified
if [ -z $VERSION ]; then
    VERSION=$(mvn -q -Dexec.executable=echo -Dexec.args='${project.version}' --non-recursive exec:exec)
fi

echo $VERSION

./common/deployDev.sh $VERSION  && ./vtk/deployDev.sh $VERSION && ./gsactivities/deployDev.sh $VERSION && ./gsusa/deployDev.sh $VERSION && ./web/deployDev.sh $VERSION 