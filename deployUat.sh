#!/bin/bash

VERSION=$1

# Get the current version if version number is not specified
if [ -z $VERSION ]; then
    VERSION=$(mvn -q -Dexec.executable=echo -Dexec.args='${project.version}' --non-recursive exec:exec)
fi

./common/deployUat.sh $VERSION  && ./web/deployUat.sh $VERSION && ./gsusa/deployUat.sh $VERSION && ./vtk/deployUat.sh $VERSION && ./gsactivities/deployUat.sh $VERSION