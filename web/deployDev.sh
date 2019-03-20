#!/bin/bash

VERSION=$1

# Get the current version if version number is not specified
if [ -z $VERSION ]; then
    VERSION=`head -1 ../VERSIONS.txt | cut -d ' ' -f 1`
fi

curl -u 'admin:e$Fz&rsBS.XZk$6F'  -X POST http://34.205.130.12:4502/crx/packmgr/service/.json/etc/packages/Girl%20Scouts/girlscouts-web-app-$VERSION.zip?cmd=delete

curl -u 'admin:e$Fz&rsBS.XZk$6F' -F file=@"$HOME/.m2/repository/org/girlscouts/aem/web/girlscouts-web-app/$VERSION/girlscouts-web-app-$VERSION.zip" -F name="girlscouts-web-app" -F force=true -F install=true http://34.205.130.12:4502/crx/packmgr/service.jsp

curl -u 'admin:e$Fz&rsBS.XZk$6F' -F file=@"$HOME/.m2/repository/org/girlscouts/aem/web/girlscouts-web-app/$VERSION/girlscouts-web-app-$VERSION.zip" -F name="girlscouts-web-app" -F force=true -F install=true http://34.236.166.152:4503/crx/packmgr/service.jsp