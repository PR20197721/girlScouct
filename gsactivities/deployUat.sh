#!/bin/bash

VERSION=$1

# Get the current version if version number is not specified
if [ -z $VERSION ]; then
    VERSION=$(mvn -q -Dexec.executable=echo -Dexec.args='${project.version}' --non-recursive exec:exec)
fi

curl -u 'admin:cH*t3uzEsT' -F file=@"$HOME/.m2/repository/org/girlscouts/aem/gsactivities/girlscouts-gsactivities-app/$VERSION/girlscouts-gsactivities-app-$VERSION.zip" -F name="girlscouts-gsactivities-app" -F force=true -F install=true http://34.201.89.117:4502/crx/packmgr/service.jsp

curl -u 'admin:cH*t3uzEsT' -F file=@"$HOME/.m2/repository/org/girlscouts/aem/gsactivities/girlscouts-gsactivities-app/$VERSION/girlscouts-gsactivities-app-$VERSION.zip" -F name="girlscouts-gsactivities-app"cd gs -F force=true -F install=true http://34.237.161.42:4503/crx/packmgr/service.jsp
