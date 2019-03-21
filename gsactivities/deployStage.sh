#!/bin/bash

VERSION=$1

# Get the current version if version number is not specified
if [ -z $VERSION ]; then
    VERSION=`head -1 ../VERSIONS.txt | cut -d ' ' -f 1`
fi

SERVER_LIST=(52.73.0.56:4503 author-girlscouts-stage-aem61.adobecqms.net:80)

for server in ${SERVER_LIST[@]}; do
	echo "Trying server $server"
	/usr/bin/nc -z `printf $server | sed -e 's/\([^:]*\):\([0-9]*\)/\1 \2/'`
	if [ $? -ne 0 ]; then
		echo "Server $server is down. Skipping..."
	else
		echo "Deploying to http://$server"
		curl -u "admin:M[R#Ezea'"'`Lb!94a' -F file=@"$HOME/.m2/repository/org/girlscouts/aem/gsactivities/girlscouts-gsactivities-app/$VERSION/girlscouts-gsactivities-app-$VERSION.zip" -F name="girlscouts-gsactivities-app" -F force=true -F install=true http://$server/crx/packmgr/service.jsp
	fi
done
