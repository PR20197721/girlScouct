#!/bin/bash

VERSION=$1

# Get the current version if version number is not specified
if [ -z $VERSION ]; then
    VERSION=$(mvn -q -Dexec.executable=echo -Dexec.args='${project.version}' --non-recursive exec:exec)
fi

curl -u 'admin:3xJ1S?SRbCVifuCp>-d1e-Ib' -F file=@"$HOME/.m2/repository/org/girlscouts/aem/vtk/girlscouts-vtk-app/$VERSION/girlscouts-vtk-app-$VERSION.zip" -F name="girlscouts-vtk-app" -F force=true -F install=true http://3.219.209.157:4502/crx/packmgr/service.jsp

curl -u 'admin:3xJ1S?SRbCVifuCp>-d1e-Ib' -F file=@"$HOME/.m2/repository/org/girlscouts/aem/vtk/girlscouts-vtk-app/$VERSION/girlscouts-vtk-app-$VERSION.zip" -F name="girlscouts-vtk-app" -F force=true -F install=true http://3.219.145.115:4503/crx/packmgr/service.jsp
curl -u 'admin:3xJ1S?SRbCVifuCp>-d1e-Ib' -F file=@"$HOME/.m2/repository/org/girlscouts/aem/vtk/girlscouts-vtk-app/$VERSION/girlscouts-vtk-app-$VERSION.zip" -F name="girlscouts-vtk-app" -F force=true -F install=true http://3.218.216.88:4503/crx/packmgr/service.jsp