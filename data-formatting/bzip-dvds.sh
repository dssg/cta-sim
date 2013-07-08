#!/bin/bash
# The BSHT data was given to use on five DVDs in zip files.
# This script converts from zip to bzip2 (which is splittable).

for d in DVD?
do
for file in "$d"/*.gz
do
echo "$file"
filename=$(basename "$file")
filename="${filename%.*}"
gunzip -c "$file" | bzip2 -9 > bz/"$filename".bz2
done
done
