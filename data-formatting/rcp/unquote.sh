#!/bin/bash
# ./unquote.sh inputdir outputdir
# Run unquote.awk over all files in inputdir, save to outputdir
for file in "$1"/*.csv
do
filename=$(basename "$file")
echo "$filename"
awk -v outdir="$2" -f unquote.awk "$file" > "$2/$filename"
done
