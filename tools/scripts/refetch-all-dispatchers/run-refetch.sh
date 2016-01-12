#!/bin/sh

CONCURRENCY=2

cat urls | xargs -n 1 -P $CONCURRENCY ./refetch.sh
