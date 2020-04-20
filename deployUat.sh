#!/bin/bash

VERSION=$1

# Get the current version if version number is not specified
if [ -z $VERSION ]; then
    VERSION=$(mvn -q -Dexec.executable=echo -Dexec.args='${project.version}' --non-recursive exec:exec)
fi

./common/deployUat.sh $VERSION  && sleep 30s

./web/deployUat.sh $VERSION && sleep 20s

./gsusa/deployUat.sh $VERSION && sleep 20s

./gsactivities/deployUat.sh $VERSION && sleep 20s

./vtk/deployUat.sh $VERSION