#!/bin/bash

VERSION=$1

# Get the current version if version number is not specified
if [ -z $VERSION ]; then
    VERSION=$(mvn -q -Dexec.executable=echo -Dexec.args='${project.version}' --non-recursive exec:exec)
fi

curl -u 'admin:2~Ov}O3m0u{8' -F file=@"$HOME/.m2/repository/org/girlscouts/aem/gsactivities/girlscouts-gsactivities-app/$VERSION/girlscouts-gsactivities-app-$VERSION.zip" -F name="girlscouts-gsactivities-app" -F force=true -F install=true http://3.233.32.66:4502/crx/packmgr/service.jsp
