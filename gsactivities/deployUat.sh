#!/bin/bash

VERSION=$1

# Get the current version if version number is not specified
if [ -z $VERSION ]; then
    VERSION=`head -1 ../VERSIONS.txt | cut -d ' ' -f 1`
fi

SERVER_LIST=(52.72.160.170:4503 52.4.127.119:4502)

for server in ${SERVER_LIST[@]}; do
	echo "Trying server $server"
	/usr/bin/nc -z `printf $server | sed -e 's/\([^:]*\):\([0-9]*\)/\1 \2/'`
	if [ $? -ne 0 ]; then
		echo "Server $server is down. Skipping..."
	else
		echo "Deploying to http://$server"
		curl -u "admin:cH*t3uzEsT" -F file=@"$HOME/.m2/repository/org/girlscouts/gsactivities/gsactivities-app/$VERSION/gsactivities-app-$VERSION.zip" -F name="gsactivities-app" -F force=true -F install=true http://$server/crx/packmgr/service.jsp
	fi
done
