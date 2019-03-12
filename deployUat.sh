#!/bin/bash

VERSION=$1

# Get the current version if version number is not specified
if [ -z $VERSION ]; then
    VERSION=`head -1 VERSIONS.txt | cut -d ' ' -f 1`
fi

./common/deployUat.sh $VERSION  && ./web/deployUat.sh $VERSION && ./gsusa/deployUat.sh $VERSION && ./vtk/deployUat.sh $VERSION && ./gsactivities/deployUat.sh $VERSION

done
