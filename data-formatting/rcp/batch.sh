#!/bin/bash
# Run a command over all csv files in one directory and output to another
# Usage:
# $ ./batch.sh "your command" inputdir outpudir
for file in "$2"/*.csv
do
filename=$(basename "$file")
echo "$filename"
eval "$1" < "$file" > "$3/$filename"
done
