#!/bin/bash

VERSION=$1

# Get the current version if version number is not specified
if [ -z $VERSION ]; then
    VERSION=$(mvn -q -Dexec.executable=echo -Dexec.args='${project.version}' --non-recursive exec:exec)
fi

SERVER_LIST=(34.237.161.42:4503 34.201.89.117:4502)

for server in ${SERVER_LIST[@]}; do
	echo "Trying server $server"
	/usr/bin/nc -z `printf $server | sed -e 's/\([^:]*\):\([0-9]*\)/\1 \2/'`
	if [ $? -ne 0 ]; then
		echo "Server $server is down. Skipping..."
	else
		echo "Deleting existing package..."
		curl -u "admin:cH*t3uzEsT"  -X POST http://$server/crx/packmgr/service/.json/etc/packages/org.girlscouts.gsusa/gsusa-app-$VERSION.zip?cmd=delete
		echo "Deploying to http://$server"
		curl -u "admin:cH*t3uzEsT" -F file=@"$HOME/.m2/repository/org/girlscouts/web/gsusa-app/$VERSION/gsusa-app-$VERSION.zip" -F name="gsusa-app" -F force=true -F install=true http://$server/crx/packmgr/service.jsp
	fi
done
