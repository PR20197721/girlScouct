#!/bin/bash
# Rebuild the backup package and download it.

SRC_PKG_GROUP=GirlScouts
SRC_PKG_NAME=content-backup
DST_PKG_NAME=girlscouts-content-backup
DEST_DIR=/Users/mike/src/testing/bk
USERNAME=admin
PASSWORD=admin

timestamp=`date "+%Y%m%d%H%M%S"`
dst_pkg_name=${DST_PKG_NAME}-${timestamp}.zip

curl -u $USERNAME:$PASSWORD -X POST http://localhost:4502/crx/packmgr/service/.json/etc/packages/${SRC_PKG_GROUP}/${SRC_PKG_NAME}.zip?cmd=build

curl -u $USERNAME:$PASSWORD http://localhost:4502/etc/packages/GirlScouts/content-backup.zip > ${DEST_DIR}/${dst_pkg_name}
