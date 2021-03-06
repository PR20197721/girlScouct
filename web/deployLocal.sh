#!/bin/bash

VERSION=$1

# Get the current version if version number is not specified
if [ -z $VERSION ]; then
    VERSION=`sed '10q;d' pom.xml | sed 's/.*<version>\(.*\)<\/version>/\1/'`
fi

SERVER_LIST=(localhost)
PORT_LIST=(4502 4503 4505 4506)

for server in ${SERVER_LIST[@]}; do
	echo "Trying server $server"
	for port in ${PORT_LIST[@]}; do
		echo "Trying server $server:$port..."
		C:\\ZIP\\Utility\\nc.exe -z $server $port
		if [ $? -ne 0 ]; then
			echo "Server $server:$port is down. Skipping..."
		else
			curl -u admin:admin -F file=@"$HOME/.m2/repository/org/girlscouts/web/girlscouts-app/$VERSION/girlscouts-app-$VERSION.zip" -F name="girlscouts-app" -F force=true -F install=true http://$server:$port/crx/packmgr/service.jsp
			
# temporarily no longer using this bootstrap data
#			curl -u admin:admin -F file=@"$HOME/.m2/repository/org/girlscouts/web/girlscouts-content/$VERSION/girlscouts-content-$VERSION.zip" -F name="girlscouts-content" -F force=true -F install=true http://$server:$port/crx/packmgr/service.jsp
		fi
	done
done
