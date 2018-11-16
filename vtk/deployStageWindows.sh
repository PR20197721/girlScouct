#!/bin/bash

VERSION=$1

# Get the current version if version number is not specified
if [ -z $VERSION ]; then
    VERSION=`head -1 ../VERSIONS.txt | cut -d ' ' -f 1`
fi

SERVER_LIST=(34.237.4.28:4503 23.22.139.200:4502)
echo "Deleting existing VTK package in author..."
curl -u "admin:M[R#Ezea'"'`Lb!94a'  -X POST http://23.22.139.200:4502/crx/packmgr/service/.json/etc/packages/Girl%20Scouts/girlscouts-vtk-app-$VERSION.zip?cmd=delete
curl -u "admin:M[R#Ezea'"'`Lb!94a' -F file=@"$HOME/.m2/repository/org/girlscouts/vtk/girlscouts-vtk-app/$VERSION/girlscouts-vtk-app-$VERSION.zip" -F name="girlscouts-app" -F force=true -F install=true http://23.22.139.200:4502/crx/packmgr/service.jsp

echo "Deleting existing VTK package in publish..."
curl -u "admin:M[R#Ezea'"'`Lb!94a'  -X POST http://34.237.4.28:4503/crx/packmgr/service/.json/etc/packages/Girl%20Scouts/girlscouts-vtk-app-$VERSION.zip?cmd=delete
curl -u "admin:M[R#Ezea'"'`Lb!94a'  -F file=@"$HOME/.m2/repository/org/girlscouts/vtk/girlscouts-vtk-app/$VERSION/girlscouts-vtk-app-$VERSION.zip" -F name="girlscouts-app" -F force=true -F install=true http://34.237.4.28:4503/crx/packmgr/service.jsp
