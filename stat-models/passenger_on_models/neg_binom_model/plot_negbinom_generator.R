# Turn Command Line Arguments On

# Command Line: Rscript distr_negbinom_generator.R /home/wdempsey/dssg-cta-project/stat-models/mcmc_output/avgsim_negbinom_on.csv

args <- commandArgs(TRUE)
parameter_pathname <- toString(args[1]) # Model Parameters
month <- as.numeric(args[2])
weekend <- as.numeric(args[3])

### Loading and Cleaning the Data and Parameter Estimates ###

params = read.csv(parameter_pathname, header = TRUE)

### Construct Explanatory Variables for Given Observation ###

alpha = as.numeric(params[1:47])
beta = as.numeric(params[48:49])
rho = as.numeric(params[50:96])
gamma = as.numeric(params[97:108])

halfhr = seq(0,24,0.5)

print("We made it to the Distr Calc")

distr = matrix(nrow = length(halfhr), ncol = 3)

log_mu = alpha+gamma[month]+beta[weekend]
mean = exp(log_mu) * rho

lowerquartile = vector(length = length(mean))
upperquartile =	vector(length =	length(mean))

for(i in 1:length(mean)) {
    lowerquartile[i] = qnbinom(0.25, mu = mean[i], size = rho[i])
    upperquartile[i] = qnbinom(0.75, mu = mean[i], size = rho[i])
}
mean = as.numeric(mean)

X = cbind(lowerquartile, round(mean,2), upperquartile)

write.table(X, "distr_negbinom_stop.csv", sep=",", row.names = FALSE, col.names = FALSE)
