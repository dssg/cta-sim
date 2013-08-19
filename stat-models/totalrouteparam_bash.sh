#!/bin/bash                                                                                                                                

# Shell Script which fits all board and alight models for a given route 
# (fits route 6 if none specified).  

# Run the Code for Both Directions by using the for loop

for i in 0 1
do

Rscript >& /dev/null tageoid_generator.R ${1:-6} $i  # generate all the stop ids for the given direction

# Run on all the ON MODELS

FILE=tageoids.csv

exec 3<&0
exec 0<$FILE

while read line
do
  (Rscript >& /dev/null passenger_on_models/neg_binom_model/passengeron_negbin_model.R ${1:-6} $i $line ) &
  # runs the negative binomial regression model for specified route, direction, and stop id
done
wait

echo 'ON Models Should be DONE'

#  Run on all the OFF MODELS

FILE=tageoids.csv

exec 3<&0
exec 0<$FILE

while read line
do
  (Rscript >& /dev/null passenger_off_models/passengeroffmodelv2.R ${1:-6} $i $line ) &
  # runs the binomial regression model for specified route, direction, and stop id
done
wait

echo 'OFF Models Should be Done'

#  Do a check to make sure all JSONs created.

FILE=tageoids.csv

exec 3<&0
exec 0<$FILE

while read line
do
  if [ ! -f passenger_off_models/json_output/boardParams_$line.json ]
  then
    (Rscript >& /dev/null passenger_off_models/passengeroffmodelv2.R ${1:-6} $i $line ) &                                                
    echo "Was missing OFF file boardParams_"$line".json"
  fi
  if [ ! -f passenger_on_models/neg_binom_model/json_output/boardParams_$line.json ]
  then
    (Rscript >& /dev/null passenger_on_models/neg_binom_model/passengeron_negbin_model.R ${1:-6} $i $line ) &                            
    echo "Was missing ON file boardParams_"$line".json"
  fi
done

done

echo "DONE"

# Take the parameter files that are at the stop level and concatenate them
# into one file for OFF and one file for ON parameters.

cat ./passenger_on_models/neg_binom_model/json_output/boardParams* > routeparamson.json
cat ./passenger_off_models/json_output/boardParams* > routeparamsoff.json

echo "CAT WORKED!"

# Post the excitement surrounding concatenation, we need to convert the cat files
# to JSON Valid files using python code.

touch final_params_on.json
touch final_params_off.json

python param_dict_join.py  routeparamson.json final_params_on.json
python param_dict_join.py  routeparamsoff.json final_params_off.json

# Clean up : we remove the route parameter files and concatenated files to save space
# on the instance.

rm routeparams*
rm ./passenger_on_models/neg_binom_model/json_output/boardParams*
rm ./passenger_off_models/json_output/boardParams*

# If the files exist, move board and alight files at the route level to an old folder
# This allows us to keep the recent history of all the board and alight params

if [-a /var/lib/ctasim/model-fit/route_level/boardParams_${1:-6}.json]
  then 
  mv /var/lib/ctasim/model-fit/route_level/boardParams_${1:-6}.json /var/lib/ctasim/model-fit/route_level/old
  fi

if [-a /var/lib/ctasim/model-fit/route_level/alightParams_${1:-6}.json]
  then
  mv /var/lib/ctasim/model-fit/route_level/alightParams_${1:-6}.json /var/lib/ctasim/model-fit/route_level/old
  fi

# SED allows us to replace the NAs in the json files with NaNs
# and changes tageoids for certain stops.  This is all for linking up with the 
# simulation properly.

sed -e 's/"NA"/NaN/g' final_params_on.json > final_paramsNaN_on.json
sed -e 's/"NA"/NaN/g' final_params_off.json > final_paramsNaN_off.json

sed -e 's/"5316"/"17705"/g' final_paramsNaN_on.json > /var/lib/ctasim/model-fit/route_level/boardParams_${1:-6}.json
sed -e 's/"5316"/"17705"/g' final_paramsNaN_off.json > /var/lib/ctasim/model-fit/route_level/alightParams_${1:-6}.json

#  Create final cumulative JSON Valid files that contain all modeled routes.

cat /var/lib/ctasim/model-fit/route_level/boardParams* > /var/lib/ctasim/model-fit/cumulative_boardParams.json
cat /var/lib/ctasim/model-fit/route_level/alightParams* > /var/lib/ctasim/model-fit/cumulative_alightParams.json

touch boardParams.json
touch alightParams.json

python param_dict_join.py  /var/lib/ctasim/model-fit/cumulative_boardParams.json /var/lib/ctasim/model-fit/boardParams.json
python param_dict_join.py  /var/lib/ctasim/model-fit/cumulative_alightParams.json /var/lib/ctasim/model-fit/alightParams.json

