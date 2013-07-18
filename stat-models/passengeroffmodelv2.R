#!/bin/Rscript

# Type this at command line: Rscript testv2.R s3://dssg-cta-data/rcp_join2/train/apc/taroute=6/direction_name=North/stop_id=17076
# This will run the code on data inside of the folder.

args <- commandArgs(TRUE)
pathname <- toString(args[1])

### Required Libraries ###

library(coda)
library(R2jags)

### Loading and Cleaning the Data ###

stop_data = read.csv(pipe(paste("bash ../util/catdir-s3.sh", pathname)), header = FALSE)

names(stop_data) <- c("serial_number","survey_date","pattern_id", "time_actual_arrive","time_actual_depart", "passengers_on","passengers_in","passengers_off")

# summary(stop_data)

actualtime <- function(x) { 
  ## Returns Time, Month, Year, and Day of Week from Date Column in Format "time_actual_arrive"

  date_time = strptime(x, format = "%I:%M:%S %p")
  time = date_time$hour + date_time$min/60 + date_time$sec/(60*60)
  return(list(time = time))
}

dateinfo <- function(x) {
  ## Returns Day, Month, Year, and Day of Week from Date Column in Format "survey_date"

  date_time = strptime(x, format = "%m/%d/%Y")
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

num_buckets <- N/interval_length-1
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

buckets_in = buckets_in[,-1]
buckets_off = buckets_off[,-1]
days = days[-1]

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

### Vectorize the Matrix ###
Ydata = round(buckets_off,0)
N = round(buckets_in,0)
buckets <- seq(1,num_buckets)

badobs <-which(N[,1] == 0 & is.na(N[,1])) 
obs <- which(N[,1] != 0)
vector.N <- N[obs,1]
vector.Y <- Ydata[obs,1]
vector.bucket <- buckets[obs]  
vector.weekend <- rep(weekend[1],length(obs))
vector.month <- rep(months[1], length(obs))

for (d in 2:num_days) {
  obs <- which(N[,d] != 0)
  vector.N <- c(vector.N,N[obs,d])
  vector.Y <- c(vector.Y,Ydata[obs,d])
  vector.bucket <- c(vector.bucket,buckets[obs])
  vector.weekend <- c(vector.weekend,rep(weekend[d],length(obs)))
  vector.month <- c(vector.month,rep(months[d], length(obs)))
}

totalobs <- length(vector.Y)

### BUGS CODE ###

if(num_months < 12) {
model.str <- 'model
{
for (i in 1:totalobs) {
    logit(p[i]) <- alpha[bucket[i]]+beta[weekend[i]]+gamma[months[i]]
    Y[i] ~ dbin(p[i], buckets_in[i])
}

beta[1] <- 0
beta[2] ~ dnorm(beta0, itau2.beta)

alpha[1] ~ dnorm(alpha0, itau2.alpha) 

for (m in 2:num_buckets) {
alpha[m] ~ dnorm(alpha[m-1], itau2.alpha)
}

gamma[1] ~ dnorm(gamma0, itau2.gamma)
for (i in 2:num_months) {
gamma[i] ~ dnorm(gamma[i-1], itau2.gamma)
}

alpha0 ~ dflat()
beta0 ~ dflat()
gamma0 ~ dflat()

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

beta[1] <- 0
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

alpha0 ~ dflat()
beta0 ~ dflat()
gamma0 ~ dflat()

itau2.alpha ~ dgamma(1,1)
itau2.beta ~ dgamma(1,1)
itau2.gamma ~ dgamma(1,1)
}'

}

model.file = file("binomial_model.bug")
writeLines(model.str, model.file)
close(model.file)

data <- list("num_buckets" = num_buckets, "num_days" = num_days, "Y" = vector.Y, "weekend" = vector.weekend, "months" = vector.month, "num_months" = num_months, 
             "buckets_in" = vector.N, "totalobs" = totalobs, bucket = vector.bucket)

inits <- list(list(alpha0 = rnorm(1,0,0.01), alpha = replicate(num_buckets,rnorm(1,0,0.1)), itau2.alpha=rgamma(1, 0.1, 10),
                   beta0 = rnorm(1,0,0.01), beta = c(NA, rnorm(1,0,0.1)), itau2.beta = rgamma(1,0.1,10),
                   gamma0 = rnorm(1,0,0.01), gamma = replicate(num_months,rnorm(1,0,0.1)), itau2.gamma = rgamma(1, 0.1, 10)
))

parameters <- c("alpha0", "alpha", "itau2.alpha", "beta0", "beta", "itau2.beta", "gamma0", "gamma", "itau2.gamma")

print("About to run Jags")

# load.sim <- rbugs(data, inits, parameters, "poisson_model.bug",
#                  verbose=T,
#                  n.chains=1, n.iter=6000,
#                  bugsWorkingDir="/tmp", cleanBugsWorkingDir = T)

load.sim <- jags(data, inits, parameters, "poisson_model.bug", n.chains=1, n.iter=2000, n.burnin=200, progress.bar="text")

load.mcmc <- as.mcmc(load.sim)

df_mcmc <- data.frame(load.mcmc[,1:49])


# Re-order month variables #

date <- dates$year + dates$month/12
actual_months <- dates$month
num_months <- length(levels(as.factor(actual_months)))
factor_months <- as.factor(date)
first_month = round(as.numeric(levels(as.factor(factor_months))[1]) %% 1 * 12,0)
obs_month = seq(first_month,first_month+num_months-1,1) %% 12
obs_month[obs_month == 0] = 12

months.mcmc = load.mcmc[,52:(52+num_months-1)]

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
write.table(total_df, "totalsim_test.csv", sep=",", row.names = FALSE, col.names = TRUE)
write.table(avg_values, "avgsim_test.csv", sep=",", row.names = FALSE, col.names = TRUE)




