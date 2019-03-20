#!/bin/bash

VERSION=$1

# Get the current version if version number is not specified
if [ -z $VERSION ]; then
    VERSION=`head -1 ../VERSIONS.txt | cut -d ' ' -f 1`
fi

SERVER_LIST=(34.237.161.42:4503 34.201.89.117:4502)

for server in ${SERVER_LIST[@]}; do
	echo "Trying server $server"
	/usr/bin/nc -z `printf $server | sed -e 's/\([^:]*\):\([0-9]*\)/\1 \2/'`
	if [ $? -ne 0 ]; then
		echo "Server $server is down. Skipping..."
	else
		echo "Deploying to http://$server"
		curl -u "admin:cH*t3uzEsT" -F file=@"$HOME/.m2/repository/org/girlscouts/aem/common/girlscouts-common-app/$VERSION/girlscouts-common-app-$VERSION.zip" -F name="girlscouts-common-app" -F force=true -F install=true http://$server/crx/packmgr/service.jsp
	fi
done
