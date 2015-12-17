#!/bin/sh

DIR=/content/gsusa/en/cookies

# clear the cache
curl -H "CQ-Action: Activate" -H "CQ-Handle: $DIR" -H "Content-Length: 0" -H "Content-Type: application/octet-stream" http://localhost/dispatcher/invalidate.cache
