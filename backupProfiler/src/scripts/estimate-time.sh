#!/bin/bash

set -e

# TODO: zbackup really deduplicates! But need intermedia storage.

# --gzip gzip
# --xz xz
# --bzip2 bzip2
# --lzip lzip
# --lzma lzma
# --lzop lzop

#source /root/backup/local-before.sh

if [ -p /tmp/backup-fifo ]; then
    rm /tmp/backup-fifo
fi

mkfifo /tmp/backup-fifo

exec 3<>/tmp/backup-fifo
(
/bin/tar -cvpP $* 2>&3 | wc -c
) 3>&1 | while read line; do dt=$(date +%s); echo "$dt $line"; done 3>&-

rm /tmp/backup-fifo

#source /root/backup/local-after.sh


