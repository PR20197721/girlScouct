#!/bin/bash

OldVersion=$(mvn -q -Dexec.executable=echo -Dexec.args='${project.version}' --non-recursive exec:exec)
NewVersion=$1
echo $OldVersion' has been updated to '$NewVersion

FileReplacementString='s/'$OldVersion'/'$NewVersion'/g'

sed -i $FileReplacementString ./VERSIONS.txt
