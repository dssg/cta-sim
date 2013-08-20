# Passenger On Model

We model passenger arrival at stops via [Negative Binomial Regression](http://ehs.sph.berkeley.edu/hubbard/longdata/webfiles/poissonmarch2005.pdf)

### File Descriptions

1. [negbinom_bash.sh](./negbinom_bash.sh) : 
2. [passengeron_negbin_model.R](./passengeron_negbin_model.R): Estimates the passenger boarding model for specified route, direction, and stop id.
  * Command Line Example: -> Rscript passengeron_negbin_model.R 6 0 1423
  * Fits the model for route 6 in direction south at stop 1423
3. [plot_negbinom_generator.R](./plot_negbinom_generator.R)
4. [validation_negbinom_on.R](./validation_negbinom_on.R)
5. [param_dict_join.py](./param_dict_join.py)
