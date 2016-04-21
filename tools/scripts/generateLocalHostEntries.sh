#!/bin/sh

# This script simply converts the load balancer alias into ip addresses and creates /etc/hosts file entries to enable vtk development on urls not in the DNS

#gsenv=( 'my-dev61:girlscout-elb1usea-1AVJ6LQH3AXDY-1374442522.us-east-1.elb.amazonaws.com' 'my-stage61:girlscout-elb1usea-1MA06QDIVAQQY-84840997.us-east-1.elb.amazonaws.com' 'my-uat61:girlscout-elb1usea-1JJZWGJR1W4PM-1508890334.us-east-1.elb.amazonaws.com' )

gsenv=( 'my-dev61:author-girlscouts-dev-aem61a.adobecqms.net' 'my-stage61:author-girlscouts-stage-aem61.adobecqms.net' 'my-uat61:author-girlscouts-uat-aem61.adobecqms.net' )

for i in "${!gsenv[@]}"; do
	thisEnv=`echo ${gsenv[$i]} | sed -e 's/^\([a-z0-9-]*\):\(.*\)/\1/'`
	thisAlias=`echo ${gsenv[$i]} | sed -e 's/^\([a-z0-9-]*\):\(.*\)/\2/'`
	dig $thisAlias | grep -v '^[\;]\|^$' | sed -e "s/.*[ 	]A[ 	]\([0-9\.]*\)$/\1/" | while read thisIP; do
		echo "$thisIP $thisEnv.girlscouts.org"
	done
done
