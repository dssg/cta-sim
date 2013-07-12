#!/bin/bash

for file in `s3ls $1`
do
    s3cat $file
done
