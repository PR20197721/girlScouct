#!/bin/bash
if [ $# -eq 0 ]; then
    echo "will generate vhost files on stage, prod and preview."
    echo "run generateAll.sh [councilurlname] [index of vhost]"
    exit 1
fi
for var in prod preview stage
do ./generateVhost-original.sh $var $1 $2
done
