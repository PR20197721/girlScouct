#!/bin/bash

VERSION=$1

# Get the current version if version number is not specified
if [ -z $VERSION ]; then
    VERSION=`sed '10q;d' pom.xml | sed 's/.*<version>\(.*\)<\/version>/\1/'`
fi

curl -u "admin:@Q&W1iHzx(" -F file=@"app/target/girlscouts-vtk-app-$VERSION.zip" -F name="girlscoutsvtk-app" -F force=true -F install=true http://54.83.199.117:4502/crx/packmgr/service.jsp
curl -u "admin:@Q&W1iHzx(" -F file=@"app/target/girlscouts-vtk-app-$VERSION.zip" -F name="girlscoutsvtk-app" -F force=true -F install=true http://54.83.199.118:4503/crx/packmgr/service.jsp
