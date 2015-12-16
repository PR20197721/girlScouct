#!/bin/sh

# Refetch pages on all dispatchers

DISPATCHERS=(52.1.110.188 52.6.178.33 54.84.146.54 54.84.248.23)
prefix=/content/gsusa
suffix=.html
path=$1

for dispatcher in "${DISPATCHERS[@]}"; do
    url=http://${dispatcher}${prefix}${path}${suffix}
    echo "$url"
    curl "$url" > /dev/null
done
