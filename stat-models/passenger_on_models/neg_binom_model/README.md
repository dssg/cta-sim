# Passenger On Model

We model passenger arrival at stops via [Negative Binomial Regression](http://ehs.sph.berkeley.edu/hubbard/longdata/webfiles/poissonmarch2005.pdf)

### File Descriptions

1. [negbinom_bash.sh](./negbinom_bash.sh) : Bash script which runs the estimation script, validation script, and then the plot scripts for specific route, direction, and stop id.
  * Command Line Example: -> bash negbinom_bash.sh 6 0 1423
2. [passengeron_negbin_model.R](./passengeron_negbin_model.R): Estimates the passenger boarding model for specified route, direction, and stop id.
  * Command Line Example: -> Rscript passengeron_negbin_model.R 6 0 1423
  * Fits the model for route 6 in direction south at stop 1423
3. [plot_negbinom_generator.R](./plot_negbinom_generator.R) : Generates plots of estimated and actual mean, 25th and 75th quantiles for specified month and week/weekend indicators.
  * Command Line Example: -> Rscript plot_negbinom_generator.R /home/wdempsey/dssg-cta-project/stat-models/passenger_on_models/neg_binom_model/mcmc_output/avgsim_negbinom_on.csv 1 1 
  * Produces plots for a given output of the Passenger ON Model and for a specified Month (in example, January) and Week/Weekend (in example, weekday)
  * Requires the passengeron_negbin_model.R to be run beforehand.
4. [validation_negbinom_on.R](./validation_negbinom_on.R) : Produces a MSE using test data and the parameter estimates from the trained regression model
  * Command Line Example: -> Rscript validation_negbinom_on.R /home/wdempsey/dssg-cta-project/stat-models/passenger_on_models/neg_binom_model/mcmc_output/avgsim_negbinom_on.csv 6 0 1423
  * Produces a MSE estimate for a given output from the boarding model for a specific route, direction, and stop id
  * Note:  The csv output from passengeron_negbin_model.R needs to match the route, direction, stop id specified in the validation command line for the results to make sense.
5. [param_dict_join.py](./param_dict_join.py) : Creates valid JSON files
  * Command Line Example: -> python param_dict_join.py
