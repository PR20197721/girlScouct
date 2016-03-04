#!/bin/bash

VERSION=$1

# Get the current version if version number is not specified
if [ -z $VERSION ]; then
    VERSION=`head -1 ../VERSIONS.txt | cut -d ' ' -f 1`
fi

curl -u "admin:@Q&W1iHzx(" -F file=@"$HOME/.m2/repository/org/girlscouts/web/girlscouts-app/$VERSION/girlscouts-app-$VERSION.zip" -F name="girlscouts-app" -F force=true -F install=true http://author-girlscouts-dev2.adobecqms.net/crx/packmgr/service.jsp
#curl -u "admin:@Q&W1iHzx(" -F file=@"$HOME/.m2/repository/org/girlscouts/web/girlscouts-content/$VERSION/girlscouts-content-$VERSION.zip" -F name="girlscouts-content" -F force=true -F install=true http://author-girlscouts-dev2.adobecqms.net/crx/packmgr/service.jsp

curl -u "admin:@Q&W1iHzx(" -F file=@"$HOME/.m2/repository/org/girlscouts/web/girlscouts-app/$VERSION/girlscouts-app-$VERSION.zip" -F name="girlscouts-app" -F force=true -F install=true http://ec2-54-83-199-118.compute-1.amazonaws.com:4503/crx/packmgr/service.jsp
#curl -u "admin:@Q&W1iHzx(" -F file=@"$HOME/.m2/repository/org/girlscouts/web/girlscouts-content/$VERSION/girlscouts-content-$VERSION.zip" -F name="girlscouts-content" -F force=true -F install=true http://ec2-54-83-199-118.compute-1.amazonaws.com:4503/crx/packmgr/service.jsp
