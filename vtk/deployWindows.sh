#!/bin/bash

VERSION=$1

# Get the current version if version number is not specified
if [ -z $VERSION ]; then
    VERSION=`head -1 ../VERSIONS.txt | cut -d ' ' -f 1`
fi

SERVER_LIST=(localhost)
PORT_LIST=(4502 4503 4504 4505 4506)

for server in ${SERVER_LIST[@]}; do
	echo "Trying server $server"
	for port in ${PORT_LIST[@]}; do
		echo "Trying server $server:$port..."
		/usr/bin/nc -z $server $port
		if [ $? -ne 0 ]; then
			echo "Server $server:$port is down. Skipping..."
		else
			curl -u 'admin:Green88!'  -X POST http://$server:$port/crx/packmgr/service/.json/etc/packages/Girl%20Scouts/girlscouts-vtk-app-$VERSION.zip?cmd=delete

			curl -u 'admin:Green88!' -F file=@"$HOME/.m2/repository/org/girlscouts/vtk/girlscouts-vtk-app/$VERSION/girlscouts-vtk-app-$VERSION.zip" -F name="girlscouts-app" -F force=true -F install=true http://$server:$port/crx/packmgr/service.jsp
		fi
	done
done

echo "Trying server 192.168.0.118:4503..."
/usr/bin/nc -z 192.168.0.118 4503
if [ $? -ne 0 ]; then
        echo "Server $server:$port is down. Skipping..."
    else
        curl -u 'admin:Green88!'  -X POST http://192.168.0.118:4503/crx/packmgr/service/.json/etc/packages/Girl%20Scouts/girlscouts-vtk-app-$VERSION.zip?cmd=delete
        curl -u 'admin:Green88!' -F file=@"$HOME/.m2/repository/org/girlscouts/vtk/girlscouts-vtk-app/$VERSION/girlscouts-vtk-app-$VERSION.zip" -F name="girlscouts-app" -F force=true -F install=true http://192.168.0.118:4503/crx/packmgr/service.jsp
fi
