#!/bin/bash

/srv/backup/estimate-time.sh --files-from ./profile-start.txt --exclude-from ./profile-unimportant.txt --exclude-from ./profile-skip.txt --gzip | tee ./important/time/profile.log
