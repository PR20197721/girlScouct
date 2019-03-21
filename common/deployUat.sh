#!/bin/bash

VERSION=$1

# Get the current version if version number is not specified
if [ -z $VERSION ]; then
    VERSION=$(mvn -q -Dexec.executable=echo -Dexec.args='${project.version}' --non-recursive exec:exec)
fi

curl -u 'admin:e$Fz&rsBS.XZk$6F' -F file=@"$HOME/.m2/repository/org/girlscouts/aem/common/girlscouts-common-app/$VERSION/girlscouts-common-app-$VERSION.zip" -F name="girlscouts-common-app" -F force=true -F install=true http://34.201.89.117:4502/crx/packmgr/service.jsp

curl -u 'admin:e$Fz&rsBS.XZk$6F' -F file=@"$HOME/.m2/repository/org/girlscouts/aem/common/girlscouts-common-app/$VERSION/girlscouts-common-app-$VERSION.zip" -F name="girlscouts-common-app" -F force=true -F install=true http://34.237.161.42:4503/crx/packmgr/service.jsp