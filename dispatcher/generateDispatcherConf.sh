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
# Generate preview dispatcher conf. Only stage has a shared dispatcher among the publisher and preview.
if [ "$env" = "stage" ] ; then
    cat conf/publish-farm.any | sed 's/publishfarm/previewfarm/' | \
                                sed 's/publish-/preview-/g' | \
                                sed 's#/docroot "/mnt/var/www/html"#/docroot "/mnt/var/www/html-preview"#' \
                                > conf/preview-farm.any
fi
find . -type f -exec env bash -c "target=../$env/{}; mustache ../$env.json {} > \$target && echo Processed {}" \;
cd ..

# Remove preview servers for stage.
if [ "$env" != "stage" ] ; then
cd $env
    find . -name preview*.any -exec rm {} \;
cd ..
fi

echo "DONE! environment: $env"
