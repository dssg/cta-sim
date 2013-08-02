# Turn Command Line Arguments On

# Command Line: Rscript plot_negbinom_generator.R /home/wdempsey/dssg-cta-project/stat-models/passenger_on_models/neg_binom_model//mcmc_output/avgsim_negbinom_on.csv 1 1 

args <- commandArgs(TRUE)
parameter_pathname <- toString(args[1]) # Model Parameters
month <- as.numeric(args[2])
weekend <- as.numeric(args[3])

input_taroute <- toString(args[4])
input_dir_group <- toString(args[5])
input_tageoid <- toString(args[6])


if(file.exists(paste("/home/wdempsey/dssg-cta-project/stat-models/passenger_off_models/distr_by_month_week/distr_on_mon_",month,"_week_",weekend,".csv", sep = ""))) {

### Loading and Cleaning the Data and Parameter Estimates ###

params = read.csv(parameter_pathname, header = TRUE)

### Redshift Connect ###

library(RODBC)

conn <- odbcConnect("dssg_cta_redshift")

stop_data<-sqlQuery(conn,paste("select * from rcp_join_dn1_train_apc where taroute='",input_taroute,"' and dir_group=",input_dir_group," and tageoid='",input_tageoid,"'", sep = ""))

names(stop_data) <- c("serial_number","survey_date","pattern_id", "time_actual_arrive","time_actual_depart", "passengers_on","passengers_in","passengers_off", "taroute","dir_group","tageoid")

pass_in = passengers_in + passengers_off - passengers_on

### Construct Explanatory Variables for Given Observation ###

alpha = as.numeric(params[1:48])
beta = as.numeric(params[49:50])
gamma = as.numeric(params[51:62])

halfhr = seq(0,24,0.5)

print("We made it to the Distr Calc")

distr = matrix(nrow = length(halfhr), ncol = 3)

log_p = alpha+gamma[month]+beta[weekend]
p = exp(log_p)

lowerquartile = vector(length = length(mean))
upperquartile =	vector(length =	length(mean))

print(p)

for(i in 1:length(mean)) {
    lowerquartile[i] = qnorm(0.25, mu = p[i], sd = sqrt(p[i] * ( 1- p[i])))
    upperquartile[i] = qnorm(0.75, mu = p[i], sd = sqrt(p[i] * ( 1- p[i])))
}

p = as.numeric(p)

bern_distr = cbind(lowerquartile, round(mean,2), upperquartile)

write.table(negbinom_distr, paste("/home/wdempsey/dssg-cta-project/stat-models/passenger_off_models/distr_by_month_week/est_distr_on_mon_",month,"_week_",weekend,".csv", sep = ""), sep=",", row.names = FALSE, col.names = FALSE)

actual_distr = read.csv(paste("/home/wdempsey/dssg-cta-project/stat-models/passenger_off_models/distr_by_month_week/distr_on_mon_",month,"_week_",weekend,".csv", sep = ""), header = FALSE)

halfhr = seq(0,24, 0.5)

shownobs = which(halfhr > 5 & halfhr < 23)


png(paste('avg_prediction_',month,'_',weekend,'.png', sep = ""), width = 1100, height = 750)
plot(halfhr[shownobs], actual_distr[shownobs,2], lty = 1, type = "l", axes = FALSE, xlab = "Hour", ylab = "Passenger On Count", ylim = c(0,45))
axis(2)
axis(1, at = seq(5,23,2))
lines(halfhr[shownobs],actual_distr[shownobs,1], lty = 2)
lines(halfhr[shownobs],actual_distr[shownobs,3], lty = 2)
lines(halfhr[shownobs],bern_distr[shownobs,2], lty = 1, col = "red")
lines(halfhr[shownobs],bern_distr[shownobs,1], lty = 2, col = "red")
lines(halfhr[shownobs],bern_distr[shownobs,3], lty = 2, col = "red")
mtext(paste("Distrubtion of Avg. Fitted and Observed Data for Month",month,"and Week/Weekday",as.numeric(weekend==2)), line = 0)
legend(7,35, c("Observed Mean", "Observed 75th/25th Percentile","Predicted Mean", "Predicted 75th/25th Percentile"), lty = c(1,2,1,2), col = c("black","black", "red", "red"), cex = 0.75)
dev.off()

}