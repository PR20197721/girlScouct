#!/bin/bash

VERSION=$1

# Get the current version if version number is not specified
if [ -z $VERSION ]; then
    VERSION=`head -1 ../../VERSIONS.txt | cut -d ' ' -f 1`
fi

curl -u admin:admin -F action=install -F bundlestartlevel=20 -F bundlefile=@"./target/girlscouts-vtk-replication-bundle-${VERSION}.jar" http://localhost:4502/system/console/bundles
curl -u admin:admin -F action=install -F bundlestartlevel=20 -F bundlefile=@"./target/girlscouts-vtk-replication-bundle-${VERSION}.jar" http://localhost:4503/system/console/bundles
curl -u admin:admin -F action=install -F bundlestartlevel=20 -F bundlefile=@"./target/girlscouts-vtk-replication-bundle-${VERSION}.jar" http://localhost:4505/system/console/bundles
curl -u admin:admin -F action=install -F bundlestartlevel=20 -F bundlefile=@"./target/girlscouts-vtk-replication-bundle-${VERSION}.jar" http://localhost:4506/system/console/bundles
