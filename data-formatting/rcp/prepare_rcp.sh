#!/bin/bash
# This is a script to pre-process the ridecheck_plus data that we received.
# Usage:
#  $ ./prepare_rcp.sh inputdir outpudir
# inputdir is the directory containing the untouched data as we received it.
# outputdir is the path for a directory to save the processed data to
inputdir=$(readlink -f "$1")
outputdir=$(readlink -f "$2")

# make outputdir
mkdir "$outputdir"
detaildir="$outputdir"/detail
mkdir "$detaildir"
masterdir="$outputdir"/master
mkdir "$masterdir"
stagedir="$outputdir"/stage
mkdir "$stagedir"
unquotedir="$outputdir"/unquote
mkdir "$unquotedir"

# split combined files
echo "Splitting combined files"
awk -v outputdir="$stagedir" -f split_pick_rcp_master_N-22toN-24.awk "$inputdir"/rcp_master_N-22toN-24.csv
awk -v outputdir="$stagedir" -f split_pick_rcp_detail_N-21_N-22.awk "$inputdir"/rcp_detail_N-21_N-22.csv


# remove extra columns from rcp_detail_N-25 and rcp_detail_N-26
echo "Reduce column set for N-25 and N-26 to same as earlier picks"
awk -f reduce_rcp_detail.awk "$inputdir"/rcp_detail_N-25.csv > "$stagedir"/rcp_detail_N-25.csv
awk -f reduce_rcp_detail.awk "$inputdir"/rcp_detail_N-26.csv > "$stagedir"/rcp_detail_N-26.csv

# link missing files in staging dir
ln -s "$inputdir"/rcp_master_N-21.csv "$stagedir"
ln -s "$inputdir"/rcp_master_N-25.csv "$stagedir"
ln -s "$inputdir"/rcp_master_N-26.csv "$stagedir"
ln -s "$inputdir"/rcp_detail_N-23.csv "$stagedir"
ln -s "$inputdir"/rcp_detail_N-24.csv "$stagedir"

# remove quotes
echo "Removing quotes"
./batch.sh "awk -f unquote.awk" "$stagedir" "$unquotedir"
rm "$stagedir"/*.csv

# some timestamps are "12/30/1899", replace these with null value
echo "Remove dates for incomplete timestamps"
./batch.sh "sed 's/12\/3[01]\/1899,/,/g'" "$unquotedir" "$stagedir" 
rm "$unquotedir"/*.csv
rmdir "$unquotedir"

# separate detail and master files
mv "$stagedir"/rcp_master*.csv "$masterdir"
mv "$stagedir"/rcp_detail*.csv "$detaildir"
rmdir "$stagedir"

head -n 1 "$inputdir"/rcp_detail_N-21_N-22.csv | sed 's/"//g' > "$outputdir"/header_rcp_detail.csv
head -n 1 "$inputdir"/rcp_master_N-21.csv | sed 's/"//g' > "$outputdir"/header_rcp_master.csv

