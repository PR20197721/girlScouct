#!/bin/bash

OldVersion=`head -1 ./VERSIONS.txt | cut -d ' ' -f 1`
NewVersion=$1
echo $OldVersion' has been updated to '$NewVersion

FileReplacementString='s/'$OldVersion'/'$NewVersion'/g'

sed -i $FileReplacementString ./VERSIONS.txt
