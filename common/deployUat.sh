#!/bin/bash

VERSION=$1

# Get the current version if version number is not specified
if [ -z $VERSION ]; then
    VERSION=$(mvn -q -Dexec.executable=echo -Dexec.args='${project.version}' --non-recursive exec:exec)
fi

curl -k -u 'admin:2~Ov}O3m0u{8' -F file=@"$HOME/.m2/repository/org/girlscouts/aem/common/girlscouts-common-app/$VERSION/girlscouts-common-app-$VERSION.zip" -F name="girlscouts-common-app" -F force=true -F install=true https://3.233.32.66/crx/packmgr/service.jsp
curl -k -u 'admin:2~Ov}O3m0u{8' -F file=@"$HOME/.m2/repository/org/girlscouts/aem/common/girlscouts-common-app/$VERSION/girlscouts-common-app-$VERSION.zip" -F name="girlscouts-common-app" -F force=true -F install=true https://34.195.250.246/crx/packmgr/service.jsp