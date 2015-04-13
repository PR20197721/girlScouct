#!/bin/bash

curl -u admin:4U5Hsq5Q_I -F action=install -F bundlestartlevel=20 -F bundlefile=@"./target/girlscouts-vtk-replication-bundle-1.0-SNAPSHOT.jar" http://54.85.69.30:4503/system/console/bundles
curl -u admin:4U5Hsq5Q_I -F action=install -F bundlestartlevel=20 -F bundlefile=@"./target/girlscouts-vtk-replication-bundle-1.0-SNAPSHOT.jar" http://54.172.117.137:4503/system/console/bundles
