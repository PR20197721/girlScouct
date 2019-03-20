#!/bin/bash

VERSION=$1

# Get the current version if version number is not specified
if [ -z $VERSION ]; then
    VERSION=`head -1 ../VERSIONS.txt | cut -d ' ' -f 1`
fi

SERVER_LIST=(34.237.4.28:4503 23.22.139.200:4502)
curl -u "admin:M[R#Ezea'"'`Lb!94a' -F file=@"$HOME/.m2/repository/org/girlscouts/aem/vtk/girlscouts-vtk-app/$VERSION/girlscouts-vtk-app-$VERSION.zip" -F name="girlscouts-vtk-app" -F force=true -F install=true http://23.22.139.200:4502/crx/packmgr/service.jsp
curl -u "admin:M[R#Ezea'"'`Lb!94a' -F file=@"$HOME/.m2/repository/org/girlscouts/aem/vtk/girlscouts-vtk-app/$VERSION/girlscouts-vtk-app-$VERSION.zip" -F name="girlscouts-vtk-app" -F force=true -F install=true http://34.237.4.28:4503/crx/packmgr/service.jsp
