# Passenger On Model

We model passenger alighting at stops via [Binomial Regression](http://en.wikipedia.org/wiki/Binomial_regression)

### File Descriptions
 
1. [passengeroffmodelv2.R](./passengeroffmodelv2.R): Estimates the passenger alighting model for specified route, direction, and stop id.
  * Command Line Example: -> Rscript passengeroffmodelv2.R 6 0 1423
  * Fits the model for route 6 in direction south at stop 1423
2. [plot_off_generator.R](./plot_off_generator.R) : Generates plots of estimated and actual mean, 25th and 75th quantiles for specified month and week/weekend indicators.
  * Command Line Example: -> Rscript plot_off_generator.R /home/wdempsey/dssg-cta-project/stat-models/passenger_off_models/mcmc_output/avgsim_negbinom_on.csv 1 1 
  * Produces plots for a given output of the Passenger ON Model and for a specified Month (in example, January) and Week/Weekend (in example, weekday)
  * Requires the passengeronoffmodelv2.R to be run beforehand.
3. [validation_off.R](./validation_off.R) : Produces a MSE using test data and the parameter estimates from the trained regression model
  * Command Line Example: -> Rscript validation_off.R /home/wdempsey/dssg-cta-project/stat-models/passenger_off_models/mcmc_output/avgsim_negbinom_on.csv 6 0 1423
  * Produces a MSE estimate for a given output from the alighting model for a specific route, direction, and stop id
  * Note:  The csv output from passengeronoffmodelv2.R needs to match the route, direction, stop id specified in the validation command line for the results to make sense.
