#!/bin/bash

VERSION=$1

# Get the current version if version number is not specified
if [ -z $VERSION ]; then
    VERSION=`head -1 VERSIONS.txt | cut -d ' ' -f 1`
fi

./common/deploy.sh $VERSION  && ./web/deploy.sh $VERSION && ./gsusa/deploy.sh $VERSION && ./vtk/deploy.sh $VERSION && ./gsactivities/deploy.sh $VERSION