#!/bin/bash
# This script migrates from /content/girlscouts-usa to /content/gateway
# For GNU sed
# This script should only be run under /content, NOT /apps

find . -name ".content.xml" -exec sed -e 's/\/content\/girlscouts-usa/\/content\/gateway/g' -i'' {} \;
