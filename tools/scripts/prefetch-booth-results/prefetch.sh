#!/bin/sh

# UPDATE DISPATCHER IPS, separated by spaces
OTHER_DISPATCHERS=(10.44.3.92 10.44.3.231 10.44.3.154)
# UPDATE SSH USERNAME
USERNAME=cse

TMP_FILE=/home/$USERNAME/booth-result-cache.tar.gz
BOOTH_RESULT_DIR=/mnt/var/www/html/content/gsusa/en/cookies
CONCURRENCY=80

echo "########## Prefetching start for cookie booth pages."
date

# clear the cache
#curl -H "CQ-Action: Activate" -H "CQ-Handle: $BOOTH_RESULT_DIR" -H "Content-Length: 0" -H "Content-Type: application/octet-stream" http://localhost/dispatcher/invalidate.cache
sudo touch /mnt/var/www/html/content/gsusa/.stat

# prefetch all zip cache
cat /home/$USERNAME/zipUrls.txt | xargs -n 1 -P $CONCURRENCY wget --spider --force-html

# tar the cached files up
cd $BOOTH_RESULT_DIR
tar -czvf $TMP_FILE cookies.*.html

# scp to other dispatchers and untar
for dispatcher in "${OTHER_DISPATCHERS[@]}"; do
    echo "Copying cache to $dispatcher"

    scp $TMP_FILE $USERNAME@$dispatcher:$TMP_FILE
    echo "After copy"
    # Add --touch so that the cache file is newer that .stat.
    ssh -t  -t  $USERNAME@$dispatcher "sudo tar -C $BOOTH_RESULT_DIR --touch -xvf $TMP_FILE"
done

echo "########## Prefetching finished for cookie booth pages."
date
