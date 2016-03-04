#!/bin/bash

function change_version {
    echo "Working on file: $FILENAME"
    { rm $FILENAME && awk "{if (NR == $LINE_NUM) print \"${PREV}${NEW_VERSION}${AFTER}\"; else print;}"  > $FILENAME; } < $FILENAME
}

# main
NEW_VERSION=$1

if [ -z $NEW_VERSION ]; then
    echo "Usage $0 NEW_VERSION"
    exit;
fi

mvn versions:set -DnewVersion=$NEW_VERSION

FILENAME=web/app/src/main/content/META-INF/vault/properties.xml
LINE_NUM=11
PREV='<entry key=\"version\">'
AFTER='</entry>'
change_version

FILENAME=web/app/src/main/content/META-INF/vault/definition/.content.xml
LINE_NUM=17
PREV='    version=\"'
AFTER='\">'
change_version

FILENAME=web/content/src/main/content/META-INF/vault/properties.xml
LINE_NUM=11
PREV='<entry key=\"version\">'
AFTER='</entry>'
change_version

FILENAME=web/content/src/main/content/META-INF/vault/definition/.content.xml
LINE_NUM=17
PREV='    version=\"'
AFTER='\">'
change_version

FILENAME=vtk/app/src/main/content/META-INF/vault/properties.xml
LINE_NUM=6
PREV='<entry key=\"version\">'
AFTER='</entry>'
change_version

FILENAME=vtk/app/src/main/content/META-INF/vault/definition/.content.xml
LINE_NUM=11
PREV='    version=\"'
AFTER='\">'
change_version

FILENAME=gsusa/bootstrap/src/main/content/META-INF/vault/properties.xml
LINE_NUM=6
PREV='<entry key=\"version\">'
AFTER='</entry>'
change_version

FILENAME=gsusa/bootstrap/src/main/content/META-INF/vault/definition/.content.xml
LINE_NUM=11
PREV='    version=\"'
AFTER='\">'
change_version

FILENAME=gsusa/content/src/main/content/META-INF/vault/properties.xml
LINE_NUM=6
PREV='<entry key=\"version\">'
AFTER='</entry>'
change_version

FILENAME=gsusa/content/src/main/content/META-INF/vault/definition/.content.xml
LINE_NUM=11
PREV='    version=\"'
AFTER='\">'
change_version

FILENAME=tools/jsp/sibling-remover/src/main/content/META-INF/vault/properties.xml
LINE_NUM=6
PREV='<entry key=\"version\">'
AFTER='\">'
change_version

FILENAME=tools/search-indexes/META-INF/vault/properties.xml
LINE_NUM=11
PREV='<entry key=\"version\">'
AFTER='\">'
change_version

FILENAME=tools/search-indexes/META-INF/vault/definition/.content.xml
LINE_NUM=17
PREV='    version=\"'
AFTER='\">'
change_version
