#!/bin/bash

VERSION=$1

# Get the current version if version number is not specified
if [ -z $VERSION ]; then
    VERSION=$(mvn -q -Dexec.executable=echo -Dexec.args='${project.version}' --non-recursive exec:exec)
fi

echo "Deleting existing VTK package in author..."
curl -u 'admin:e$Fz&rsBS.XZk$6F'  -X POST http://34.205.130.12:4502/crx/packmgr/service/.json/etc/packages/Girl%20Scouts/girlscouts-vtk-app-$VERSION.zip?cmd=delete
curl -u 'admin:e$Fz&rsBS.XZk$6F' -F file=@"$HOME/.m2/repository/org/girlscouts/vtk/girlscouts-vtk-app/$VERSION/girlscouts-vtk-app-$VERSION.zip" -F name="girlscouts-app" -F force=true -F install=true http://34.205.130.12:4502/crx/packmgr/service.jsp

echo "Deleting existing VTK package in publish..."

curl -u 'admin:e$Fz&rsBS.XZk$6F'  -X POST http://34.236.166.152:4503/crx/packmgr/service/.json/etc/packages/Girl%20Scouts/girlscouts-vtk-app-$VERSION.zip?cmd=delete
curl -u 'admin:e$Fz&rsBS.XZk$6F' -F file=@"$HOME/.m2/repository/org/girlscouts/vtk/girlscouts-vtk-app/$VERSION/girlscouts-vtk-app-$VERSION.zip" -F name="girlscouts-app" -F force=true -F install=true http://34.236.166.152:4503/crx/packmgr/service.jsp