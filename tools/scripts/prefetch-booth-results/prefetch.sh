#!/bin/sh

# UPDATE DISPATCHER IPS, separated by spaces
OTHER_DISPATCHERS=(10.44.3.138)
# UPDATE SSH USERNAME
USERNAME=mzhou

TMP_FILE=/home/$USERNAME/booth-result-cache.tar.gz
BOOTH_RESULT_DIR=/mnt/var/www/html/content/gsusa/en/cookies
CONCURRENCY=100

# clear the cache
curl -H "CQ-Action: Activate" -H "CQ-Handle: $BOOTH_RESULT_DIR" -H "Content-Length: 0" -H "Content-Type: application/octet-stream" http://localhost/dispatcher/invalidate.cache

# prefetch all zip cache
cat zipUrls.txt | xargs -n 1 -P $CONCURRENCY wget --spider --force-html

# tar the cached files up
cd $BOOTH_RESULT_DIR
tar -czvf $TMP_FILE cookies.*.html

# scp to other dispatchers and untar
for dispatcher in "${OTHER_DISPATCHERS[@]}"; do
    echo "Copying cache to $dispatcher"
    scp $TMP_FILE $USERNAME@$dispatcher:$TMP_FILE
    ssh $USERNAME@$dispatcher "touch $BOOTH_RESULT_DIR/.stat; sleep 5; tar -C $BOOTH_RESULT_DIR -xvf $TMP_FILE"
done
