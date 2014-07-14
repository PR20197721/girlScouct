#!/bin/bash
# This script migrates from foundation components to girlscouts components
# For GNU sed
# This script should only be run under /content, NOT /apps

find . -name '.content.xml' -exec bash -c "sed -e 's/\bfoundation\/components\/text\b/girlscouts\/components\/text/g' -i'' {};\
                                           sed -e 's/\bfoundation\/components\/textimage\b/girlscouts\/components\/textimage/g' -i'' {};\
                                           sed -e 's/\bfoundation\/components\/image\b/girlscouts\/components\/image/g' -i'' {};" \;
