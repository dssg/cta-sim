#!/bin/Rscript

# Type each of the following in the same line of the terminal:
# Rscript passengeroffmodelv2.R 6 0 1423
# /home/wdempsey/dssg-cta-project/stat-models/passenger_on_models/neg_binom_model/mcmc_output/totalsim_negbinom_on.csv
# /home/wdempsey/dssg-cta-project/stat-models/passenger_on_models/neg_binom_model/mcmc_output/avgsim_negbinom_on.csv

# This will run the code on data inside of the folder.


### Command Line Arguments ###

args <- commandArgs(TRUE)

input_taroute <- toString(args[1])
input_dir_group <- toString(args[2])
input_tageoid <- toString(args[3])

# totaloutput <- toString(args[4])
# avgoutput <- toString(args[5])

# input_months <- as.numeric(args[6])
# input_weekend <- as.numeric(args[7])

### Required Libraries ###

library(coda)
library(R2jags)
library(rjson)

### Loading and Cleaning the Data ###

### Redshift Connect ###

library(RODBC)

conn <- odbcConnect("dssg_cta_redshift")

stop_data<-sqlQuery(conn,paste("select * from rcp_join_dn1_train_apc where taroute='",input_taroute,"' and dir_group=",input_dir_group," and tageoid='",input_tageoid,"'", sep = ""))

names(stop_data) <- c("serial_number","survey_date","pattern_id", "time_actual_arrive","time_actual_depart", "passengers_on","passengers_in","passengers_off", "taroute","dir_group","tageoid")

odbcClose(conn)

dim(stop_data)

summary(stop_data)

### Cleaning ###

actualtime <- function(x) { 
  ## Returns Time, Month, Year, and Day of Week from Date Column in Format "time_actual_arrive"

  date_time = strptime(x, format = "%H:%M:%S")
  time = date_time$hour + date_time$min/60 + date_time$sec/(60*60)
  return(list(time = time))
}

dateinfo <- function(x) {
  ## Returns Day, Month, Year, and Day of Week from Date Column in Format "survey_date"

  date_time = strptime(x, format = "%Y-%m-%d")
  day = date_time$mday
  month = date_time$mon
  year = date_time$year
  wday = date_time$wday
  return(list(month = month, year = year, wday = wday, day = day))
}

stop_data$time <- actualtime(stop_data$time_actual_arrive)$time

### Bucketing By 30 Minute Intervals ###

# Buckets are 30 Minute Intervals and Observations are Per Day so bucket_off[t,d] is number of passengers getting
# off the bus in the half hour interval.

interval_length = 30/60 # interval length in hours
N = 24 # numer of hours in day
days = levels(as.factor(stop_data$survey_date))

num_buckets <- N/interval_length
num_days = length(levels(as.factor(stop_data$survey_date)))

buckets_off = matrix(nrow = num_buckets, ncol = num_days)
bucket_times <- seq(0,N,interval_length)

print("Done With Functions and pre-bucketing")

for (d in 1:num_days) {
  for(t in 1:num_buckets) {
    if(length(which(stop_data$time > bucket_times[t] & stop_data$time <= bucket_times[t+1] & stop_data$survey_date == days[d])) == 0 ) {
      buckets_off[t,d] <- 0
    }
    if(length(which(stop_data$time > bucket_times[t] & stop_data$time <= bucket_times[t+1] & stop_data$survey_date == days[d])) != 0) {
      buckets_off[t,d] <- sum(stop_data$passengers_off[stop_data$time > bucket_times[t] & stop_data$time <= bucket_times[t+1] & stop_data$survey_date == days[d]])	
    }
  }
}

buckets_in = matrix(nrow = num_buckets, ncol = num_days) # Contains the Total Number of People on the Bus at Arrival

for (d in 1:num_days) {
  for(t in 1:num_buckets) {
    if(length(which(stop_data$passengers_in > 0 & stop_data$time > bucket_times[t] & stop_data$time <= bucket_times[t+1] & stop_data$survey_date == days[d])) == 0 ) {
      buckets_in[t,d] <- 0
    }
    if(length(which(stop_data$passengers_in > 0 & stop_data$time > bucket_times[t] & stop_data$time <= bucket_times[t+1] & stop_data$survey_date == days[d])) != 0) {
      buckets_in[t,d] <- max(sum(stop_data$passengers_in[stop_data$passengers_in > 0 & stop_data$time > bucket_times[t] & stop_data$time <= bucket_times[t+1] & stop_data$survey_date == days[d]]) - sum(stop_data$passengers_on[stop_data$passengers_in > 0 & stop_data$time > bucket_times[t] & stop_data$time <= bucket_times[t+1] & stop_data$survey_date == days[d]]) + sum(stop_data$passengers_off[stop_data$passengers_in > 0 & stop_data$time > bucket_times[t] & stop_data$time <= bucket_times[t+1] & stop_data$survey_date == days[d]]),0)
    }
  }
}

# buckets_in = buckets_in[,-1]
# buckets_off = buckets_off[,-1]
# days = days[-1]

num_days = length(days)

dates <- dateinfo(days)

# Month Variable -> Need to Order By Earliest Month-Year Observed

date <- dates$year + dates$month/12
actual_months <- dates$month
factor_months <- as.factor(date)
num_months <- length(levels(as.factor(factor_months)))
levels(factor_months) <- strsplit(toString(seq(1,num_months)), ", ")[[1]]

months <- as.numeric(factor_months) %% 12
months[months == 0] <- 12

day_of_week <- dates$wday
weekend <- as.numeric(day_of_week == 0 | day_of_week == 6)+1

empty_days = vector(length = num_days)

for(i in 1:num_days){empty_days[i] <- max(buckets_in[,i])}

### Calculate Distr for Input_month and Input_weekend ###

#print("About to Calc Distributions")

#print(paste("Actual Levels of Months is :",levels(as.factor(actual_months))))

#for (i in 1:12) {
#for (j in 1:2) {

#distr_obs = which(actual_months == i-1 & weekend == j)

#if (length(distr_obs) != 0 ){

#distr_buckets = matrix(ncol = 3, nrow = num_buckets)

#print(c(i,j))

#for(k in 1:num_buckets) {
#    distr_buckets[k,2] <- mean(buckets_in[k,distr_obs] / buckets_off[k,distr_obs], na.rm = TRUE)
#    distr_buckets[k,1] <- quantile(buckets_in[k,distr_obs] / buckets_off[k,distr_obs],0.25, na.rm = TRUE)
#    distr_buckets[k,3] <- quantile(buckets_in[k,distr_obs] / buckets_off[k,distr_obs],0.75, na.rm = TRUE)
#}

#write.table(distr_buckets, paste("mcmc_output/distr_on_mon_",i,"_week_",j,".csv",sep = ""), sep=",", row.names = FALSE, col.names = FALSE)

#}
#}
#}

#print("Finished Calcing Distributions")

### Vectorize the Matrix ###
Ydata = round(buckets_off,0)
N = round(buckets_in,0)
buckets <- seq(1,num_buckets)

badobs <-which(N[,1] == 0 & is.na(N[,1])) 
obs <- which(N[,1] != 0 & is.na(N[,1]) == FALSE)
vector.N <- N[obs,1]
vector.Y <- Ydata[obs,1]
vector.bucket <- buckets[obs]  
vector.weekend <- rep(weekend[1],length(obs))
vector.month <- rep(months[1], length(obs))

for (d in 2:num_days) {
  obs <- which(N[,d] != 0 & is.na(N[,1]) == FALSE)
  vector.N <- c(vector.N,N[obs,d])
  vector.Y <- c(vector.Y,Ydata[obs,d])
  vector.bucket <- c(vector.bucket,buckets[obs])
  vector.weekend <- c(vector.weekend,rep(weekend[d],length(obs)))
  vector.month <- c(vector.month,rep(months[d], length(obs)))
}

badobs = which(vector.N < vector.Y)

if (length(badobs) != 0) {
vector.N = vector.N[-badobs]
vector.Y = vector.Y[-badobs]
vector.bucket = vector.bucket[-badobs]
vector.weekend = vector.weekend[-badobs]
vector.month = vector.month[-badobs]
}

totalobs <- length(vector.Y)

print(paste("Number of Observations we're using:",length(totalobs)))

### BUGS CODE ###

if(num_months < 12) {
model.str <- 'model
{
for (i in 1:totalobs) {
    logit(p[i]) <- alpha[bucket[i]]+beta[weekend[i]]+gamma[months[i]]
    Y[i] ~ dbin(p[i], buckets_in[i])
}

beta[1] ~ dnorm(beta0, itau2.beta)
beta[2] ~ dnorm(beta0, itau2.beta)

alpha[1] ~ dnorm(alpha0, itau2.alpha) 

for (m in 2:num_buckets) {
alpha[m] ~ dnorm(alpha[m-1], itau2.alpha)
}

gamma[1] ~ dnorm(gamma0, itau2.gamma)
for (i in 2:num_months) {
gamma[i] ~ dnorm(gamma[i-1], itau2.gamma)
}

alpha0 ~ dnorm(0,10)
beta0 ~ dnorm(0,10)
gamma0 ~ dnorm(0,10)

itau2.alpha ~ dgamma(1,1)
itau2.beta ~ dgamma(1,1)
itau2.gamma ~ dgamma(1,1)
}'
}

if (num_months == 12) {

model.str <- 'model
{
for (i in 1:totalobs) {
    logit(p[i]) <- alpha[bucket[i]]+beta[weekend[i]]+gamma[months[i]]
    Y[i] ~ dbin(p[i], buckets_in[i])
}

beta[1] ~ dnorm((beta0, itau2.beta)
beta[2] ~ dnorm(beta0, itau2.beta)

alpha[1] ~ dnorm(alpha0, itau2.alpha)

for (m in 2:num_buckets) {
alpha[m] ~ dnorm(alpha[m-1], itau2.alpha)
}

gamma[1] ~ dnorm(gamma0, itau2.gamma)
for (i in 2:(num_months-1)) {
gamma[i] ~ dnorm(gamma[i-1], itau2.gamma)
}

gamma[num_months] ~ dnorm((gamma[num_months-1]+gamma[1])/2, itau2.gamma)

alpha0 ~ dnorm(0,10)
beta0 ~ dnorm(0,10)
gamma0 ~ dnorm(0,10)

itau2.alpha ~ dgamma(1,1)
itau2.beta ~ dgamma(1,1)
itau2.gamma ~ dgamma(1,1)
}'

}

model.file = file("binomial_model.bug")
writeLines(model.str, model.file)
close(model.file)

# check = cbind(vector.Y,vector.N,vector.bucket,vector.weekend,vector.month)

data <- list("num_buckets" = num_buckets, "Y" = vector.Y, "weekend" = vector.weekend, "months" = vector.month, "num_months" = num_months, 
             "buckets_in" = vector.N, "totalobs" = totalobs, bucket = vector.bucket)

inits <- list(list(alpha0 = rnorm(1,0,0.01), alpha = replicate(num_buckets,rnorm(1,0,0.1)), itau2.alpha=rgamma(1, 0.1, 10),
                   beta0 = rnorm(1,0,0.01), beta = rnorm(2,0,0.1), itau2.beta = rgamma(1,0.1,10),
                   gamma0 = rnorm(1,0,0.01), gamma = replicate(num_months,rnorm(1,0,0.1)), itau2.gamma = rgamma(1, 0.1, 10)
))

parameters <- c("alpha0", "alpha", "itau2.alpha", "beta0", "beta", "itau2.beta", "gamma0", "gamma", "itau2.gamma")

print("About to run Jags")

# load.sim <- rbugs(data, inits, parameters, "poisson_model.bug",
#                  verbose=T,
#                  n.chains=1, n.iter=6000,
#                  bugsWorkingDir="/tmp", cleanBugsWorkingDir = T)

load.sim <- jags(data, inits, parameters, "binomial_model.bug", n.chains=1, n.iter=2000, n.burnin=200, progress.bar="text")

print(load.sim)

load.mcmc <- as.mcmc(load.sim)

df_mcmc <- data.frame(load.mcmc[,c(1:48,50:51)])

# df_mcmc <- data.frame(load.mcmc)

# Re-order month variables #

date <- dates$year + dates$month/12
actual_months <- dates$month
num_months <- length(levels(as.factor(actual_months)))
factor_months <- as.factor(date)
first_month = round(as.numeric(levels(as.factor(factor_months))[1]) %% 1 * 12,0)+1

print(paste("First Month is",first_month))

obs_month = seq(first_month,first_month+num_months-1,1) %% 12
obs_month[obs_month == 0] = 12

print(paste("Observed Months are",obs_month))

months.mcmc = load.mcmc[,54:(54+num_months-1)]

dim(load.mcmc)
print(months.mcmc[1,])

months.mcmc = data.frame(months.mcmc)

months= matrix(nrow = dim(months.mcmc)[1], ncol = 12)

for (i in 1:12) {
    if (length(which(obs_month == i)) == 0) {
       months[,i] = NA
    }
    if (length(which(obs_month == i)) != 0) {
       months[,i] = months.mcmc[,which(obs_month == i)]
    }
}

months = data.frame(months)

names(months) = strsplit(toString(seq(1,12)), ", ")[[1]]

total_df = cbind(df_mcmc, months)

avg_values = total_df[1,]

for (i in 1:dim(total_df)[2]) {
    avg_values[,i] = mean(total_df[,i])
}

# write to file
#write.table(total_df, "mcmc_output/totalsim_off.csv", sep=",", row.names = FALSE, col.names = TRUE)
#write.table(avg_values, "mcmc_output/avgsim_off.csv", sep=",", row.names = FALSE, col.names = TRUE)

### JSON OUTPUT ### 

print("About to print JSON Output")

if(length(unique(stop_data$taroute))==1){
  taroute=unique(stop_data$taroute)
}else{
  print("ERROR: route not unique")
}
if(length(unique(stop_data$dir_group))==1){
  dir_group=unique(stop_data$dir_group)
}else{
  print("ERROR: dir_group not unique")
}
if(length(unique(stop_data$tageoid))==1){
  tageoid=unique(stop_data$tageoid)
}else{
  print("ERROR: tageoid not unique")
}

#PARAMETERS
values = as.vector(as.matrix(avg_values))

nTimeOfDay=list(values[1:48])
names(nTimeOfDay)=c("llTimeOfDay")
nDayType=list(values[49:50])
names(nDayType)=c("llDayType")
nMonth=list(values[51:62])
names(nMonth)= c("llMonth")

param_list = c(nTimeOfDay, nDayType, nMonth)

#TAGEOID
cL=list(param_list)
names(cL)=c(tageoid)

#DIR_GROUP
bL=list(cL)
names(bL)=c(dir_group)

#TAROUTE
aL=list(bL)
names(aL)=c(taroute)

#FIT DATE
final=list(aL)
#names(final)=c(Sys.Date())
names(final) = c("6/18/2013")

json = toJSON(final)

# setwd("cta-webapp/src/main/resources/")

print("Made it to Creating the JSON File")

print(paste("json_output/boardParams_",input_tageoid,".json",sep = ""))

write(json, file=paste("/home/wdempsey/dssg-cta-project/stat-models/passenger_off_models/json_output/boardParams_",input_tageoid,".json",sep = ""), append = TRUE)

print("Created JSON File")



