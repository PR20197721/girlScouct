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
                        if [ $port -eq 4502 ]; then
				echo "Disabling Author Dam Update Asset and  Dam MetaData Writeback workflows"
				curl -u admin:admin "http://$server:$port/system/console/components/com.adobe.granite.workflow.core.launcher.WorkflowLauncherImpl/com.adobe.granite.workflow.core.launcher.WorkflowLauncherImpl" -H 'Accept: application/json, text/javascript, */*; q=0.01' -H 'Cache-Control: no-cache' --data 'action=disable'
                                curl -u admin:admin "http://$server:$port/system/console/components/com.adobe.granite.workflow.core.launcher.WorkflowLauncherListener/com.adobe.granite.workflow.core.launcher.WorkflowLauncherListener" -H 'Accept: application/json, text/javascript, */*; q=0.01' -H 'Cache-Control: no-cache' --data 'action=disable'
			fi

			# Deploy Package
			curl -u admin:admin -F file=@"$HOME/.m2/repository/org/girlscouts/web/gsusa-bootstrap/$VERSION/gsusa-bootstrap-$VERSION.zip" -F name="girlscouts-app" -F force=true -F install=true http://$server:$port/crx/packmgr/service.jsp

			# Enable image renditions workflow
			if [ $port -eq 4502 ]; then
                                echo "Re-enabling Author Dam Update Asset and  Dam MetaData Writeback workflows"
                                curl -u admin:admin "http://$server:$port/system/console/components/com.adobe.granite.workflow.core.launcher.WorkflowLauncherImpl/com.adobe.granite.workflow.core.launcher.WorkflowLauncherImpl" -H 'Accept: application/json, text/javascript, */*; q=0.01' -H 'Cache-Control: no-cache' --data 'action=enable'
                                curl -u admin:admin "http://$server:$port/system/console/components/com.adobe.granite.workflow.core.launcher.WorkflowLauncherListener/com.adobe.granite.workflow.core.launcher.WorkflowLauncherListener" -H 'Accept: application/json, text/javascript, */*; q=0.01' -H 'Cache-Control: no-cache' --data 'action=enable'
			fi
		fi
	done
done
