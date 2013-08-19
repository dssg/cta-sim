#!/bin/bash                                                                                                                                
 
#set -m

for i in 0 1
do

Rscript >& /dev/null tageoid_generator.R ${1:-6} $i

# Run on all the ON MODELS

FILE=tageoids.csv

exec 3<&0
exec 0<$FILE

while read line
do
  (Rscript >& /dev/null passenger_on_models/neg_binom_model/passengeron_negbin_model.R ${1:-6} $i $line ) &
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
done
wait

echo 'OFF Models SHould be Done'

#  Do a Final JSON CHECK

FILE=tageoids.csv

exec 3<&0
exec 0<$FILE

while read line
do
  if [ ! -f passenger_off_models/json_output/boardParams_$line.json ]
  then
    (Rscript >& /dev/null passenger_off_models/passengeroffmodelv2.R ${1:-6} $i $line ) &                                                
    echo "Missing OFF file boardParams_"$line".json"
  fi
  if [ ! -f passenger_on_models/neg_binom_model/json_output/boardParams_$line.json ]
  then
    (Rscript >& /dev/null passenger_on_models/neg_binom_model/passengeron_negbin_model.R ${1:-6} $i $line ) &                            
    echo "Missing ON file boardParams_"$line".json"
  fi
done


done

echo "DONE"

cat ./passenger_on_models/neg_binom_model/json_output/boardParams* > routeparamson.json
cat ./passenger_off_models/json_output/boardParams* > routeparamsoff.json

echo "CAT WORKED!"

touch final_params_on.json
touch final_params_off.json

python param_dict_join.py  routeparamson.json final_params_on.json
python param_dict_join.py  routeparamsoff.json final_params_off.json

# Clean up
rm routeparams*
rm ./passenger_on_models/neg_binom_model/json_output/boardParams*
rm ./passenger_off_models/json_output/boardParams*

sed -e 's/"NA"/NaN/g' final_params_on.json > final_paramsNaN_on.json
sed -e 's/"NA"/NaN/g' final_params_off.json > final_paramsNaN_off.json

if [-a /var/lib/ctasim/model-fit/route_level/boardParams_${1:-6}.json]
  then 
  mv /var/lib/ctasim/model-fit/route_level/boardParams_${1:-6}.json /var/lib/ctasim/model-fit/route_level/old
  fi

if [-a /var/lib/ctasim/model-fit/route_level/alightParams_${1:-6}.json]
  then
  mv /var/lib/ctasim/model-fit/route_level/alightParams_${1:-6}.json /var/lib/ctasim/model-fit/route_level/old
  fi

sed -e 's/"5316"/"17705"/g' final_paramsNaN_on.json > /var/lib/ctasim/model-fit/route_level/boardParams_${1:-6}.json
sed -e 's/"5316"/"17705"/g' final_paramsNaN_off.json > /var/lib/ctasim/model-fit/route_level/alightParams_${1:-6}.json

cat /var/lib/ctasim/model-fit/route_level/boardParams* > /var/lib/ctasim/model-fit/cumulative_boardParams.json
cat /var/lib/ctasim/model-fit/route_level/alightParams* > /var/lib/ctasim/model-fit/cumulative_alightParams.json

touch boardParams.json
touch alightParams.json

python param_dict_join.py  /var/lib/ctasim/model-fit/cumulative_boardParams.json /var/lib/ctasim/model-fit/boardParams.json
python param_dict_join.py  /var/lib/ctasim/model-fit/cumulative_alightParams.json /var/lib/ctasim/model-fit/alightParams.json

