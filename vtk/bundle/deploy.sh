#!/bin/bash

curl -u admin:admin -F action=install -F bundlestartlevel=20 -F bundlefile=@"./target/girlscouts-vtk-bundle-1.0-SNAPSHOT.jar" http://localhost:4502/system/console/bundles
curl -u admin:admin -F action=install -F bundlestartlevel=20 -F bundlefile=@"./target/girlscouts-vtk-bundle-1.0-SNAPSHOT.jar" http://localhost:4503/system/console/bundles
curl -u admin:admin -F action=install -F bundlestartlevel=20 -F bundlefile=@"./target/girlscouts-vtk-bundle-1.0-SNAPSHOT.jar" http://localhost:4505/system/console/bundles
curl -u admin:admin -F action=install -F bundlestartlevel=20 -F bundlefile=@"./target/girlscouts-vtk-bundle-1.0-SNAPSHOT.jar" http://localhost:4506/system/console/bundles
