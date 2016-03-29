#!/bin/bash

VERSION=$1

# Get the current version if version number is not specified
if [ -z $VERSION ]; then
    VERSION=`sed '10q;d' pom.xml | sed 's/.*<version>\(.*\)<\/version>/\1/'`
fi

SERVER_LIST=(54.85.69.30:4503 54.86.13.38:4502)

for server in ${SERVER_LIST[@]}; do
	echo "Trying server $server"
	/usr/bin/nc -z `printf $server | sed -e 's/\([^:]*\):\([0-9]*\)/\1 \2/'`
	if [ $? -ne 0 ]; then
		echo "Server $server is down. Skipping..."
	else
		echo "Deploying to http://$server"
		curl -u "admin:4U5Hsq5Q_I" -F file=@"$HOME/.m2/repository/org/girlscouts/web/girlscouts-app/$VERSION/girlscouts-app-$VERSION.zip" -F name="girlscouts-app" -F force=true -F install=true http://$server/crx/packmgr/service.jsp
	fi
done
