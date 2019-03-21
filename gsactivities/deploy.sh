#!/bin/bash

VERSION=$1

# Get the current version if version number is not specified
if [ -z $VERSION ]; then
    VERSION=$(mvn -q -Dexec.executable=echo -Dexec.args='${project.version}' --non-recursive exec:exec)
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
			curl -u admin:admin -F file=@"$HOME/.m2/repository/org/girlscouts/aem/gsactivities/girlscouts-gsactivities-app/$VERSION/girlscouts-gsactivities-app-$VERSION.zip" -F name="girlscouts-gsactivities-app" -F force=true -F install=true http://$server:$port/crx/packmgr/service.jsp
		fi
	done
done
