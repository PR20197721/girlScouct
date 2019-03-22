#!/bin/bash

VERSION=$1

# Get the current version if version number is not specified
if [ -z $VERSION ]; then
    VERSION=$(mvn -q -Dexec.executable=echo -Dexec.args='${project.version}' --non-recursive exec:exec)
fi

echo "Deleting existing VTK package in publish..."
curl -u "admin:M[R#Ezea'"'`Lb!94a'  -F file=@"$HOME/.m2/repository/org/girlscouts/vtk/girlscouts-vtk-app/$VERSION/girlscouts-vtk-app-$VERSION.zip" -F name="girlscouts-app" -F force=true -F install=true http://34.237.4.28:4503/crx/packmgr/service.jsp
