#!/bin/bash

VERSION=$1

# Get the current version if version number is not specified
if [ -z $VERSION ]; then
    VERSION=$(mvn -q -Dexec.executable=echo -Dexec.args='${project.version}' --non-recursive exec:exec)
fi

curl -u 'admin:2~Ov}O3m0u{8' -X POST http://3.230.103.184:4502/crx/packmgr/service/.json/etc/packages/org.girlscouts.gsusa/gsusa-app-$VERSION.zip?cmd=delete
curl -u 'admin:2~Ov}O3m0u{8' -F file=@"$HOME/.m2/repository/org/girlscouts/web/gsusa-app/$VERSION/gsusa-app-$VERSION.zip" -F name="gsusa-app" -F force=true -F install=true http://3.230.103.184:4502/crx/packmgr/service.jsp

curl -u 'admin:2~Ov}O3m0u{8' -X POST http://52.23.100.13:4503/crx/packmgr/service/.json/etc/packages/org.girlscouts.gsusa/gsusa-app-$VERSION.zip?cmd=delete
curl -u 'admin:2~Ov}O3m0u{8' -F file=@"$HOME/.m2/repository/org/girlscouts/web/gsusa-app/$VERSION/gsusa-app-$VERSION.zip" -F name="gsusa-app" -F force=true -F install=true http://52.23.100.13:4503/crx/packmgr/service.jsp
