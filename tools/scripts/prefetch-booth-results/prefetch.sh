#!/bin/sh

BOOTH_RESULT_DIR=/mnt/var/www/html/content/gsusa/en/cookies
TMP_FILE=/home/mzhou/booth-result-cache.tar.gz
CONCURRENCY=120
USERNAME=username
OTHER_DISPATCHERS=(10.44.3.138)

# clear the cache
curl -H "CQ-Action: Activate" -H "CQ-Handle: $BOOTH_RESULT_DIR" -H "Content-Length: 0" -H "Content-Type: application/octet-stream" http://localhost/dispatcher/invalidate.cache

# prefetch all zip cache
cat zipUrls.txt | xargs -n 1 -P $CONCURRENCY wget --spider --force-html

# tar the cached files up
pushd .
cd $BOOTH_RESULT_DIR
tar -czf $TMP_FILE *
popd

# scp to other dispatchers and untar
for dispatcher in "${OTHER_DISPATCHERS[@]}"; do
    echo "Copying cache to $dispatcher"
    scp $TMP_FILE $USERNAME@$dispatcher:$TMP_FILE
    ssh $USERNAME@$dispatcher "tar -C $BOOTH_RESULT_DIR -xf $TMP_FILE"
done
