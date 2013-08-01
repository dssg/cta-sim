# Turn Command Line Arguments On

# Command Line: Rscript plot_negbinom_generator.R /home/wdempsey/dssg-cta-project/stat-models/passenger_on_models/neg_binom_model//mcmc_output/avgsim_negbinom_on.csv 1 1 

args <- commandArgs(TRUE)
parameter_pathname <- toString(args[1]) # Model Parameters
month <- as.numeric(args[2])
weekend <- as.numeric(args[3])

if(file.exists(paste("/home/wdempsey/dssg-cta-project/stat-models/passenger_on_models/neg_binom_model/distr_by_month_week/distr_on_mon_",month,"_week_",weekend,".csv", sep = ""))) {

### Loading and Cleaning the Data and Parameter Estimates ###

print(parameter_pathname)

params = read.csv(parameter_pathname, header = TRUE)

### Construct Explanatory Variables for Given Observation ###

alpha = as.numeric(params[1:48])
beta = as.numeric(params[49:50])
rho = as.numeric(params[51:98])
gamma = as.numeric(params[99:110])

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

negbinom_distr = cbind(lowerquartile, round(mean,2), upperquartile)

write.table(negbinom_distr, paste("/home/wdempsey/dssg-cta-project/stat-models/passenger_on_models/neg_binom_model/distr_by_month_week/est_distr_on_mon_",month,"_week_",weekend,".csv", sep = ""), sep=",", row.names = FALSE, col.names = FALSE)

actual_distr = read.csv(paste("/home/wdempsey/dssg-cta-project/stat-models/passenger_on_models/neg_binom_model/distr_by_month_week/distr_on_mon_",month,"_week_",weekend,".csv", sep = ""), header = FALSE)

halfhr = seq(0,24, 0.5)

shownobs = which(halfhr > 5 & halfhr < 23)


png(paste('avg_prediction_',month,'_',weekend,'.png', sep = ""), width = 1100, height = 750)
plot(halfhr[shownobs], actual_distr[shownobs,2], lty = 1, type = "l", axes = FALSE, xlab = "Hour", ylab = "Passenger On Count", ylim = c(0,45))
axis(2)
axis(1, at = seq(5,23,2))
lines(halfhr[shownobs],actual_distr[shownobs,1], lty = 2)
lines(halfhr[shownobs],actual_distr[shownobs,3], lty = 2)
lines(halfhr[shownobs],negbinom_distr[shownobs,2], lty = 1, col = "red")
lines(halfhr[shownobs],negbinom_distr[shownobs,1], lty = 2, col = "red")
lines(halfhr[shownobs],negbinom_distr[shownobs,3], lty = 2, col = "red")
mtext(paste("Distrubtion of Avg. Fitted and Observed Data for Month",month,"and Week/Weekday",as.numeric(weekend==2)), line = 0)
legend(7,35, c("Observed Mean", "Observed 75th/25th Percentile","Predicted Mean", "Predicted 75th/25th Percentile"), lty = c(1,2,1,2), col = c("black","black", "red", "red"), cex = 0.75)
dev.off()

}