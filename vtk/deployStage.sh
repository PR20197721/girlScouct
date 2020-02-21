#!/bin/bash

VERSION=$1

# Get the current version if version number is not specified
if [ -z $VERSION ]; then
    VERSION=$(mvn -q -Dexec.executable=echo -Dexec.args='${project.version}' --non-recursive exec:exec)
fi

curl -u 'admin:L9pqlq@tz#>sSsARQOW69drS' -F file=@"$HOME/.m2/repository/org/girlscouts/aem/vtk/girlscouts-vtk-app/$VERSION/girlscouts-vtk-app-$VERSION.zip" -F name="girlscouts-vtk-app" -F force=true -F install=true http://34.236.224.243:4502/crx/packmgr/service.jsp

curl -u 'admin:L9pqlq@tz#>sSsARQOW69drS' -F file=@"$HOME/.m2/repository/org/girlscouts/aem/vtk/girlscouts-vtk-app/$VERSION/girlscouts-vtk-app-$VERSION.zip" -F name="girlscouts-vtk-app" -F force=true -F install=true http://3.211.191.77:4503/crx/packmgr/service.jsp
curl -u 'admin:L9pqlq@tz#>sSsARQOW69drS' -F file=@"$HOME/.m2/repository/org/girlscouts/aem/vtk/girlscouts-vtk-app/$VERSION/girlscouts-vtk-app-$VERSION.zip" -F name="girlscouts-vtk-app" -F force=true -F install=true http://54.209.234.176:4503/crx/packmgr/service.jsp