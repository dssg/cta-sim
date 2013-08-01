#!/bin/bash

echo "Running the Model Fitting via MCMC"

Rscript >& /dev/null passengeron_negbin_model.R ${3:-s3://dssg-cta-data/rcp_join2/train/apc/taroute=6/direction_name=South/stop_id=1984} ${4:-./mcmc_output/totalsim_negbinom_on.csv} ${5:-./mcmc_output/avgsim_negbinom_on.csv} ${1:-1} ${2-1}

echo "Finished Fitting! Onto Validation"

Rscript validation_negbinom_on.R ${5:-./mcmc_output/avgsim_negbinom_on.csv} ${3:-s3://dssg-cta-data/rcp_join2/test/apc/taroute=6/direction_name=South/stop_id=1984}

echo "Validation Complete!  Onto Distribution Output and Visualization"

Rscript >& /dev/null plot_negbinom_generator.R ${5:-./mcmc_output/avgsim_negbinom_on.csv} ${1:-1} ${2:-1}

Rscript &> /dev/null plot_mcmc.R ${2:-./mcmc_output/totalsim_negbinom_on.csv} ${1:-1} ${2:-1}

mv total_prediction_* avg_prediction_* mcmc_plots/