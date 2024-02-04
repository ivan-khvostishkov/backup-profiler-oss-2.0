#!/bin/bash

set -e

# Override with export
if [ -z $BACKUP_FILE ]; then
	BACKUP_FILE='/dev/null'
fi

time /bin/tar -cvpPf $BACKUP_FILE --files-from ./profile-start.txt --exclude-from ./profile-unimportant.txt --exclude-from ./profile-skip.txt --gzip 2>&1 | tee ./backup.log


