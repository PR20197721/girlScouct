#!/bin/bash

VERSION=$1

# Get the current version if version number is not specified
if [ -z $VERSION ]; then
    VERSION=`head -1 ../../VERSIONS.txt | cut -d ' ' -f 1`
fi

echo "Disabling Author Dam Update Asset and  Dam MetaData Writeback workflows"

curl -u "admin:@Q&W1iHzx(" "http://54.83.199.117:4502/system/console/components/com.adobe.granite.workflow.core.launcher.WorkflowLauncherImpl/com.adobe.granite.workflow.core.launcher.WorkflowLauncherImpl" -H 'Accept: application/json, text/javascript, */*; q=0.01' -H 'Cache-Control: no-cache' --data 'action=disable'

curl -u "admin:@Q&W1iHzx(" "http://54.83.199.117:4502/system/console/components/com.adobe.granite.workflow.core.launcher.WorkflowLauncherListener/com.adobe.granite.workflow.core.launcher.WorkflowLauncherListener" -H 'Accept: application/json, text/javascript, */*; q=0.01' -H 'Cache-Control: no-cache' --data 'action=disable'

echo ""
echo "Uploading Package..."

curl -u "admin:@Q&W1iHzx(" -F file=@"$HOME/.m2/repository/org/girlscouts/web/gsusa-bootstrap/$VERSION/gsusa-bootstrap-$VERSION.zip" -F name="gsusa-bootstrap" -F force=true -F install=true http://author-girlscouts-dev2.adobecqms.net/crx/packmgr/service.jsp

echo "Re-enabling Author Dam Update Asset and  Dam MetaData Writeback workflows"

curl -u "admin:@Q&W1iHzx(" "http://54.83.199.117:4502/system/console/components/com.adobe.granite.workflow.core.launcher.WorkflowLauncherImpl/com.adobe.granite.workflow.core.launcher.WorkflowLauncherImpl" -H 'Accept: application/json, text/javascript, */*; q=0.01' -H 'Cache-Control: no-cache' --data 'action=enable'

curl -u "admin:@Q&W1iHzx(" "http://54.83.199.117:4502/system/console/components/com.adobe.granite.workflow.core.launcher.WorkflowLauncherListener/com.adobe.granite.workflow.core.launcher.WorkflowLauncherListener" -H 'Accept: application/json, text/javascript, */*; q=0.01' -H 'Cache-Control: no-cache' --data 'action=enable'

echo ""
echo "Uploading Package to Publish..."

curl -u "admin:@Q&W1iHzx(" -F file=@"$HOME/.m2/repository/org/girlscouts/web/gsusa-bootstrap/$VERSION/gsusa-bootstrap-$VERSION.zip" -F name="gsusa-bootstrap" -F force=true -F install=true http://ec2-54-83-199-118.compute-1.amazonaws.com:4503/crx/packmgr/service.jsp
