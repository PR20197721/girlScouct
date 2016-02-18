#!/bin/bash

VERSION=$1

# Get the current version if version number is not specified
if [ -z $VERSION ]; then
    VERSION=`sed '10q;d' pom.xml | sed 's/.*<version>\(.*\)<\/version>/\1/'`
fi

curl -u 'admin:e$Fz&rsBS.XZk$6F' -F file=@"$HOME/.m2/repository/org/girlscouts/web/gsusa-content/$VERSION/gsusa-content-$VERSION.zip" -F name="gsusa-content" -F force=true -F install=true http://52.71.87.139:4502/crx/packmgr/service.jsp

curl -u 'admin:e$Fz&rsBS.XZk$6F' -F file=@"$HOME/.m2/repository/org/girlscouts/web/gsusa-content/$VERSION/gsusa-content-$VERSION.zip" -F name="gsusa-content" -F force=true -F install=true http://52.1.73.148:4503/crx/packmgr/service.jsp

