#!/bin/bash

VERSION=$1

# Get the current version if version number is not specified
if [ -z $VERSION ]; then
    VERSION=`head -1 ../../VERSIONS.txt | cut -d ' ' -f 1`
    fi

SERVER_LIST=(localhost)
PORT_LIST=(4502 4503 4505 4506)

for server in ${SERVER_LIST[@]}; do
        echo "Trying server $server"
        for port in ${PORT_LIST[@]}; do
                echo "Trying server $server:$port..."
                /usr/bin/nc -z $server $port
                if [ $? -ne 0 ]; then
                        echo "Server $server:$port is down. Skipping..."
                else
                        curl -u admin:admin -F action=install -F bundlestartlevel=20 -F bundlefile=@"./target/girlscouts-vtk-replication-bundle-${VERSION}.jar" http://$server:$port/system/console/bundles
                fi
        done
done
