#!/bin/bash

shopt -s globstar || exit

for file in $1/**
do
    cat $file
done
