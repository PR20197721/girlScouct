#!/bin/bash

VERSION=$1

# Get the current version if version number is not specified
if [ -z $VERSION ]; then
    VERSION=`head -1 ../../VERSIONS.txt | cut -d ' ' -f 1`
fi

SERVER_LIST=(54.86.13.38:4502 54.85.69.30:4503)

for server in ${SERVER_LIST[@]}; do
	echo "Trying server $server"
	/usr/bin/nc -z `printf $server | sed -e 's/\([^:]*\):\([0-9]*\)/\1 \2/'`
	if [ $? -ne 0 ]; then
		echo "Server $server is down. Skipping..."
	else
		if [ $server == '54.86.13.38:4502' ]; then
			echo "Disabling Author Dam Update Asset and  Dam MetaData Writeback workflows"
			curl -u "admin:4U5Hsq5Q_I" "http://$server/system/console/components/com.adobe.granite.workflow.core.launcher.WorkflowLauncherImpl/com.adobe.granite.workflow.core.launcher.WorkflowLauncherImpl" -H 'Accept: application/json, text/javascript, */*; q=0.01' -H 'Cache-Control: no-cache' --data 'action=disable'
			curl -u "admin:4U5Hsq5Q_I" "http://$server/system/console/components/com.adobe.granite.workflow.core.launcher.WorkflowLauncherListener/com.adobe.granite.workflow.core.launcher.WorkflowLauncherListener" -H 'Accept: application/json, text/javascript, */*; q=0.01' -H 'Cache-Control: no-cache' --data 'action=disable'
		fi
		echo ""
		echo "Deploying to http://$server"
		curl -u "admin:4U5Hsq5Q_I" -F file=@"$HOME/.m2/repository/org/girlscouts/web/gsusa-bootstrap/$VERSION/gsusa-bootstrap-$VERSION.zip" -F name="gsusa-bootstrap" -F force=true -F install=true http://$server/crx/packmgr/service.jsp

		if [ $server == '54.86.13.38:4502' ]; then
			echo "Re-enabling Author Dam Update Asset and  Dam MetaData Writeback workflows"
			curl -u "admin:4U5Hsq5Q_I" "http://$server/system/console/components/com.adobe.granite.workflow.core.launcher.WorkflowLauncherImpl/com.adobe.granite.workflow.core.launcher.WorkflowLauncherImpl" -H 'Accept: application/json, text/javascript, */*; q=0.01' -H 'Cache-Control: no-cache' --data 'action=enable'
			curl -u "admin:4U5Hsq5Q_I" "http://$server/system/console/components/com.adobe.granite.workflow.core.launcher.WorkflowLauncherListener/com.adobe.granite.workflow.core.launcher.WorkflowLauncherListener" -H 'Accept: application/json, text/javascript, */*; q=0.01' -H 'Cache-Control: no-cache' --data 'action=enable'
		fi
	fi
done
