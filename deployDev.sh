#!/bin/bash

VERSION=$1

# Get the current version if version number is not specified
if [ -z $VERSION ]; then
    VERSION=`head -1 VERSIONS.txt | cut -d ' ' -f 1`
fi

./common/deployDev.sh $VERSION  && ./web/deployDev.sh $VERSION && ./gsusa/deployDev.sh $VERSION && ./vtk/deployDev.sh $VERSION && ./gsactivities/deployDev.sh $VERSION