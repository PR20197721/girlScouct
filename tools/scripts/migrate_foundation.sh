#!/bin/bash
# This script migrates from foundation components to girlscouts components
# This is for Mac. For GNU sed, replace [[:<:]] and [[:>:]] with \b
# This script should only be run under /content, NOT /apps

find . -name '.content.xml' -exec bash -c "sed -i'' -e 's/[[:<:]]foundation\/components\/text[[:>:]]/girlscouts\/components\/text/g' {};\
                                           sed -i'' -e 's/[[:<:]]foundation\/components\/textimage[[:>:]]/girlscouts\/components\/textimage/g' {};\
                                           sed -i'' -e 's/[[:<:]]foundation\/components\/image[[:>:]]/girlscouts\/components\/image/g' {};" \;
