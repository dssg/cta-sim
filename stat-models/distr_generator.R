# Turn Command Line Arguments On

# Command Line: Rscript distr_generator.R /home/wdempsey/dssg-cta-project/stat-models/mcmc_output/avgsim_on.csv

args <- commandArgs(TRUE)
parameter_pathname <- toString(args[1]) # Model Parameters
month <- as.numeric(args[2])
weekend <- as.numeric(args[3])

### Loading and Cleaning the Data and Parameter Estimates ###

params = read.csv(parameter_pathname, header = TRUE)

### Construct Explanatory Variables for Given Observation ###

alpha = params[1:47]
beta = params[48:49]
gamma = params[50:61]

halfhr = seq(0,24,0.5)

print("We made it to the Distr Calc")

distr = matrix(nrow = length(halfhr), ncol = 3)

rate = as.vector(exp(as.vector(alpha)+as.numeric(gamma[month])+as.numeric(beta[weekend])))

lowerquartile = vector(length = length(rate))
upperquartile =	vector(length =	length(rate))

for(i in 1:length(rate)) {
    lowerquartile[i] = qpois(0.25, as.numeric(rate[i]))
    upperquartile[i] = qpois(0.75, as.numeric(rate[i]))
}
rate = as.numeric(rate)

X = cbind(lowerquartile, round(rate,2), upperquartile)

write.table(X, "distr_stop.csv", sep=",", row.names = FALSE, col.names = FALSE)
