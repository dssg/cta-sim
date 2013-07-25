#!/bin/bash
# Output all data with an S3 prefix to stdout
# Prepend the partition values as columns in each line

for file in `s3ls $1`
do
    prefix=$(echo "$file" | tr "/" "\n" | sed -n 's/^.*=\(.*\)/\1/p' | tr "\n" ",")
    s3cat $file | sed "s/^/$prefix/"
done
