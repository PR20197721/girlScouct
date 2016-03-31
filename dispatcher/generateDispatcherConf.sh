#!/bin/bash
# Generate dispatcher config based on the template
# Param: [dev/stage/uat/prod1/prod2/preview]
# Prerequisite: install mustache: brew install node; npm install -g mustache

if [ $# -eq 0 ]; then
    echo "Missing env. See the script."
    exit 1
fi

env=$1
mkdir -p $env/vhost.d
mkdir -p $env/conf.d
mkdir -p $env/conf

cd template
echo "Processing environment: $env"
find . -type f -exec env bash -c "target=../$env/{}; mustache ../$env.json {} > \$target && echo Processed {}" \;
cd ..
echo "DONE! environment: $env"
