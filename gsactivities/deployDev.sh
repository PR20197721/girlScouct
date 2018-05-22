#!/bin/bash

VERSION=$1

# Get the current version if version number is not specified
if [ -z $VERSION ]; then
    VERSION=`head -1 ../VERSIONS.txt | cut -d ' ' -f 1`
fi

curl -u 'admin:e$Fz&rsBS.XZk$6F' -F file=@"$HOME/.m2/repository/org/girlscouts/gsactivities/gsactivities-app/$VERSION/gsactivities-app-$VERSION.zip" -F name="gsactivities" -F force=true -F install=true http://34.205.130.12:4502/crx/packmgr/service.jsp

curl -u 'admin:e$Fz&rsBS.XZk$6F' -F file=@"$HOME/.m2/repository/org/girlscouts/gsactivities/gsactivities-app/$VERSION/gsactivities-app-$VERSION.zip" -F name="gsactivities" -F force=true -F install=true http://34.236.166.152:4503/crx/packmgr/service.jsp
