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

mvn versions:set -DnewVersion=$NEW_VERSION -DgenerateBackupPoms=false -P changeVersion

echo ${NEW_VERSION} `date +%m/%d/%y` > newtmp
echo "TODO: enter version description here." >> newtmp
echo "" >> newtmp
cat newtmp VERSIONS.txt > tmp
mv tmp VERSIONS.txt
rm newtmp

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

FILENAME=web/bootstrap/src/main/content/META-INF/vault/properties.xml
LINE_NUM=11
PREV='<entry key=\"version\">'
AFTER='</entry>'
change_version

FILENAME=web/bootstrap/src/main/content/META-INF/vault/definition/.content.xml
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

FILENAME=gsusa/app/src/main/content/META-INF/vault/properties.xml
LINE_NUM=6
PREV='<entry key=\"version\">'
AFTER='</entry>'
change_version

FILENAME=gsusa/app/src/main/content/META-INF/vault/definition/.content.xml
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

echo "###################################################################"
echo "Don't forget to enter version description in the VERSIONS.txt file!"
echo "###################################################################"
