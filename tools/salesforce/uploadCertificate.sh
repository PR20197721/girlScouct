#!/bin/sh

CERT_FILE=`pwd`/SelfSignedCert_15Dec2014_220134.crt

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
			curl -u admin:admin -F idp_cert=\<$CERT_FILE -F idp_cert@TypeHint=Binary http://$server:$port/etc/key/saml
                fi
        done
done

