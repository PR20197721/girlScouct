#!/bin/bash
# This script migrates from foundation components to girlscouts components
# /libs/foundation/components/text
# /libs/foundation/components/textimage
# /libs/foundation/components/image

find . -name '.content.xml' -exec bash -c "sed -i'' -e 's/foundation\/components\/text/girlscouts\/components\/text/g' {};\
                                           sed -i'' -e 's/foundation\/components\/textimage/girlscouts\/components\/textimage/g' {};\
                                           sed -i'' -e 's/foundation\/components\/image/girlscouts\/components\/image/g' {};" \;
