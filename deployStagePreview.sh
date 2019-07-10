#!/bin/bash

VERSION=$1

# Get the current version if version number is not specified
if [ -z $VERSION ]; then
    VERSION=$(mvn -q -Dexec.executable=echo -Dexec.args='${project.version}' --non-recursive exec:exec)
fi

./common/deployStagePreview.sh $VERSION  && sleep 20s

./web/deployStagePreview.sh $VERSION && sleep 20s

./gsusa/deployStagePreview.sh $VERSION && sleep 20s

./vtk/deployStagePreview.sh $VERSION
