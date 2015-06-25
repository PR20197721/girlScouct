#!/bin/bash

VERSION=$1

# Get the current version if version number is not specified
if [ -z $VERSION ]; then
    VERSION=`sed '10q;d' ../pom.xml | sed 's/.*<version>\(.*\)<\/version>/\1/'`
fi

curl -u "admin:@Q&W1iHzx(" -F file=@"$HOME/.m2/repository/org/girlscouts/web/gsusa-bootstrap/$VERSION/gsusa-bootstrap-$VERSION.zip" -F name="gsusa-bootstrap" -F force=true -F install=true http://author-girlscouts-dev2.adobecqms.net/crx/packmgr/service.jsp

curl -u "admin:@Q&W1iHzx(" -F file=@"$HOME/.m2/repository/org/girlscouts/web/gsusa-bootstrap/$VERSION/gsusa-bootstrap-$VERSION.zip" -F name="gsusa-bootstrap" -F force=true -F install=true http://ec2-54-83-199-118.compute-1.amazonaws.com:4503/crx/packmgr/service.jsp
