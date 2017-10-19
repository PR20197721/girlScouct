#!/bin/bash

VERSION=$1

# Get the current version if version number is not specified
if [ -z $VERSION ]; then
    VERSION=`head -1 ../VERSIONS.txt | cut -d ' ' -f 1`
fi

curl  -u 'admin:e$Fz&rsBS.XZk$6F'  -X POST http://34.205.130.12:4502/crx/packmgr/service/.json/etc/packages/org.girlscouts.web.gsusa/gsusa-app-$VERSION.zip?cmd=delete
curl -u 'admin:e$Fz&rsBS.XZk$6F' -F file=@"$HOME/.m2/repository/org/girlscouts/web/gsusa-app/$VERSION/gsusa-app-$VERSION.zip" -F name="gsusa-app" -F force=true -F install=true http://52.71.87.139:4502/crx/packmgr/service.jsp

curl  -u 'admin:e$Fz&rsBS.XZk$6F'  -X POST http://34.236.166.152:4503/crx/packmgr/service/.json/etc/packages/org.girlscouts.web.gsusa/gsusa-app-$VERSION.zip?cmd=delete
curl -u 'admin:e$Fz&rsBS.XZk$6F' -F file=@"$HOME/.m2/repository/org/girlscouts/web/gsusa-app/$VERSION/gsusa-app-$VERSION.zip" -F name="gsusa-app" -F force=true -F install=true http://52.1.73.148:4503/crx/packmgr/service.jsp
