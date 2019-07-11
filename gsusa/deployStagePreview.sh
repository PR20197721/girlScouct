#!/bin/bash

VERSION=$1

# Get the current version if version number is not specified
if [ -z $VERSION ]; then
    VERSION=$(mvn -q -Dexec.executable=echo -Dexec.args='${project.version}' --non-recursive exec:exec)
fi
curl -u 'admin:3xJ1S?SRbCVifuCp>-d1e-Ib' -F file=@"$HOME/.m2/repository/org/girlscouts/aem/gsusa/girlscouts-gsusa-app/$VERSION/girlscouts-gsusa-app-$VERSION.zip" -F name="girlscouts-gsusa-app" -F force=true -F install=true http://3.219.51.127:4503/crx/packmgr/service.jsp