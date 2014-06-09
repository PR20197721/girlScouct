#!/bin/bash

curl -u admin:admin -F action=install -F bundlestartlevel=20 -F bundlefile=@"./target/girlscouts-bundle-0.1-SNAPSHOT.jar" http://localhost:4502/system/console/bundles
