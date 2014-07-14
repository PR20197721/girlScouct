#!/bin/sh

java -Xmx2048m -XX:MaxPermSize=750M -Xdebug -Xrunjdwp:transport=dt_socket,server=y,address=30304,suspend=n -jar cq56-publish-p4503.jar -v -nofork  -ll 3


