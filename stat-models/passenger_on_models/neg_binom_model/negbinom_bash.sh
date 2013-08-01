#!/bin/bash

echo "Running the Model Fitting via MCMC"

Rscript >& /dev/null passengeron_negbin_model.R ${3:-6} ${4:-0} ${5:-1423} ${6:-/home/wdempsey/dssg-cta-project/stat-models/passenger_on_models/neg_binom_model/mcmc_output/totalsim_negbinom_on.csv} ${7:-/home/wdempsey/dssg-cta-project/stat-models/passenger_on_models/neg_binom_model/mcmc_output/avgsim_negbinom_on.csv}

echo "Finished Fitting! Onto Validation"

Rscript validation_negbinom_on.R ${7:-/home/wdempsey/dssg-cta-project/stat-models/passenger_on_models/neg_binom_model/mcmc_output/avgsim_negbinom_on.csv} ${3:-6} ${4:-0} ${5:-1423}

echo "Validation Complete!  Onto Distribution Output and Visualization"

Rscript >& /dev/null plot_negbinom_generator.R ${7:-/home/wdempsey/dssg-cta-project/stat-models/passenger_on_models/neg_binom_model/mcmc_output/avgsim_negbinom_on.csv} ${1:-1} ${2:-1}

# Rscript &> /dev/null plot_mcmc.R ${2:-./mcmc_output/totalsim_negbinom_on.csv} ${1:-1} ${2:-1}

mv total_prediction_* avg_prediction_* mcmc_plots/