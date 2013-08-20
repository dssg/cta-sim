# Statistical Models

### 

### File Descriptions:

1. [tageoid_generator.R](./tageoid_generator.R) : Generates a csv file called tageoids.csv containing all Stop IDs for a specified Route and Direction
  * Command Line Example: -> Rscript tageoid_generator.R 6 0
  * Generates csv for route 6 and direction south.
2. [totalrouteparam_bash.sh](./totalrouteparam_bash.sh) : Runs both OFF and ON models for a specified route. Then generates a valid JSON file which is attached to a larger JSON dictionary of all route level parameter estimates maintained on the instance.
  * Command Line Example: -> bash totalrouteparam_bash.sh 6
  * Fits both ON and OFF models for route 6 at all stops going north and south.
  * If you look in /var/lib/ctasim/model-fit/ you would find the boardParams.json and alightParams.json containing all the parameter values.
3. [param_dict_join.py](./param_dict_join.py) : Takes in a file of concatenated stop level params and creates a valid JSON file to be used in the simulation.
  * Command Line Example: -> python param_dict_join.py  routeparamson.json final_params_on.json
  * Generates a valid JSON file called final_params_on.json from a route level file.
  * Used inside of [totalrouteparam_bash.sh](./totalrouteparam_bash.sh)


### Subdirectories: 
1. [OFF Model](./passenger_off_models/) : Directory containing the necessary code for fitting the Alighting Models for specific route, direction, and stop id.
2. [ON Model](./passenger_on_models/) : Directory containing the necessary code for fitting the Boarding Models for specific route, direction, and stop id.
3. [Supply Model](./supplyside_models/) : Directory containing the necessary code for fitting the Schedule Deviation Models for specific route, direction.

