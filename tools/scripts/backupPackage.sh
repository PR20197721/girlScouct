#!/bin/bash
# This script backs up a package, download, rename it and cleanup obsolete packages.
# Original written to backup NASA data on stage pub.

DIR=/home/mzhou/NASA-bk
PORT=4503
PACKAGE=NASA-vtk-data-backup
USER=
PASS=
CLEANUP_DAYS=7

cd $DIR
curl -u "$USER:$PASS" -X POST http://localhost:$PORT/crx/packmgr/service/.json/etc/packages/my_packages/$PACKAGE.zip?cmd=build
sleep 3
curl -u "$USER:$PASS" http://localhost:$PORT/etc/packages/my_packages/$PACKAGE.zip > tmp.zip
mv tmp.zip $PACKAGE-`date +"%m%d%Y"`.zip
find . -name "$PACKAGE-*.zip" -type f -mtime +$CLEANUP_DAYS -exec rm {} \;
