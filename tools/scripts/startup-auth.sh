#!/bin/sh

java -Xmx2048m -XX:MaxPermSize=750M -Xdebug -Xrunjdwp:transport=dt_socket,server=y,address=30303,suspend=n -jar -DauthEnabled=false cq56-author-4502.jar -v -nofork -gui -ll 3


