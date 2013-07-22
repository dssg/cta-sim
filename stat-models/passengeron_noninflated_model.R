#!/bin/Rscript

# Type this at command line: Rscript passengeron_noninflated_model.R s3://dssg-cta-data/rcp_join2/train/apc/taroute=6/direction_name=North/stop_id=17076
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

# Buckets are 30 Minute Intervals and Observations are Per Day so bucket[t,d] is number of passengers getting on
# the bus in the half hour interval

# Currently only looks at the Time of Arrival at the Stop
# Future Iteration will take into account if ToA is in bucket but previous ToA is outside the bucket
# Will have to Include a Proportional Allotting to the Different Buckets

interval_length = 30/60 # interval length in hours
N = 24 # numer of hours in day
days = levels(as.factor(stop_data$survey_date))

num_buckets <- N/interval_length-1 # Number of Buckets
num_days = length(levels(as.factor(stop_data$survey_date))) # Number of Days

buckets = matrix(nrow = num_buckets, ncol = num_days)
bucket_times <- seq(0,N,interval_length)

print("Done With Functions and pre-bucketing")

for (d in 1:num_days) {
  for(t in 1:num_buckets) {
    if(length(which(stop_data$time > bucket_times[t] & stop_data$time <= bucket_times[t+1] & stop_data$survey_date == days[d])) == 0 ) {
      buckets[t,d] <- 0
    }
    if(length(which(stop_data$time > bucket_times[t] & stop_data$time <= bucket_times[t+1] & stop_data$survey_date == days[d])) != 0) {
      buckets[t,d] <- sum(stop_data$passengers_on[stop_data$time > bucket_times[t] & stop_data$time <= bucket_times[t+1] & stop_data$survey_date == days[d]])	
    }
  }
}

# Removing Days that Have No Observed Counts #

num_days = dim(buckets)[2]

emptyday = vector(length = num_days)  # Check to see if the Days are NonEmpty

for(i in 1:num_days) {emptyday[i] <- as.numeric(max(buckets[,i])==0)}

badobs = which(emptyday == 1 | is.na(emptyday))

buckets = buckets[,-badobs]
days = days[-badobs]

print("Done with Bucketing")

num_days = length(days) # Number of Days

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
weekend <- as.numeric(day_of_week == 0 | day_of_week == 6)+1  # Create Weekend Indicator

print(summary(cbind(weekend,months)))

### BUGS CODE ###

# If Number of Months is less than 12, then we have a disconnect in dependency #

if(num_months < 12) {
model.str <- 'model
{
for (m in 1:num_buckets) {
  for (d in 1:num_days) {
    log(mu[m,d]) <- alpha[m]+beta[weekend[d]]+gamma[months[d]]
    Y[m, d] ~ dpois(mu[m, d])
  }
}

beta[1] <- 0
beta[2] ~ dnorm(beta0, itau2.beta)

alpha[1] ~ dnorm(0, itau2.alpha) 

for (m in 2:num_buckets) {
alpha[m] ~ dnorm(alpha[m-1], itau2.alpha)
}

gamma[1] ~ dnorm(gamma0, itau2.gamma)
for (i in 2:num_months) {
gamma[i] ~ dnorm(gamma[i-1], itau2.gamma)
}

beta0 ~ dnorm(0,10)
gamma0 ~ dnorm(0,10)

itau2.alpha ~ dgamma(1,1)
itau2.beta ~ dgamma(1,1)
itau2.gamma ~ dgamma(1,1)
}'
}


# If the Number of Months is 12, then we need to include a loop in dependency #
# We assume that Month 0 is Marginally N(0,\sigma), then Month i is conditionally normal N( X_{i-1}, \sigma) 
# for months i <12, and then for the final month we have it is conditionally normal N( (X_{11} + X_{0})/2, \sigma) 
# Which creates a loop of dependency.

if (num_months == 12) {

model.str <- 'model
{
for (m in 1:num_buckets) {
  for (d in 1:num_days) {
    log(mu[m,d]) <- alpha[m]+beta[weekend[d]]+gamma[months[d]]
    Y[m, d] ~ dpois(mu[m,d])
  }
}

beta[1] <- 0
beta[2] ~ dnorm(beta0, itau2.beta)

alpha[1] ~ dnorm(0, itau2.alpha)

for (m in 2:num_buckets) {
alpha[m] ~ dnorm(alpha[m-1], itau2.alpha)
}

gamma[1] ~ dnorm(gamma0, itau2.gamma)
for (i in 2:(num_months-1)) {
gamma[i] ~ dnorm(gamma[i-1], itau2.gamma)
}

gamma[num_months] ~ dnorm((gamma[num_months-1]+gamma[1])/2, itau2.gamma)

beta0 ~ dnorm(0, 10)
gamma0 ~ dnorm(0, 10)

itau2.alpha ~ dgamma(1,1)
itau2.beta ~ dgamma(1,1)
itau2.gamma ~ dgamma(1,1)
}'

}

model.file = file("model_poisson.bug")
writeLines(model.str, model.file)
close(model.file)

Ydata = round(buckets,0)

data <- list("num_buckets" = num_buckets, "num_days" = num_days, "Y" = Ydata, "weekend" = weekend, "months" = months, "num_months" = num_months)

inits <- list(list(alpha = replicate(num_buckets,rnorm(1,0,0.1)), itau2.alpha=rgamma(1, 0.1, 10),
                   beta0 = rnorm(1,0,0.01), beta = c(NA, rnorm(1,0,0.1)), itau2.beta = rgamma(1,0.1,10),
                   gamma0 = rnorm(1,0,0.01), gamma = replicate(num_months,rnorm(1,0,0.1)), itau2.gamma = rgamma(1, 0.1, 10)
))
parameters <- c("alpha", "itau2.alpha", "beta0", "beta", "itau2.beta", "gamma0", "gamma", "itau2.gamma")

print("About to run Rbugs")

# load.sim <- rbugs(data, inits, parameters, "modeltest.bug",
#                  verbose=T,
#                  n.chains=1, n.iter=1000,
#                  bugsWorkingDir="/tmp", cleanBugsWorkingDir = T)


load.sim <- jags(data, inits, parameters, "model_poisson.bug", n.chains=1, n.iter=2000, n.burnin=200, progress.bar="text")

print(load.sim)

load.mcmc <- as.mcmc(load.sim)

df_mcmc <- data.frame(load.mcmc[,1:49])


# Re-order month variables #

date <- dates$year + dates$month/12
actual_months <- dates$month
num_months <- length(levels(as.factor(actual_months)))
factor_months <- as.factor(date)
first_month = round(as.numeric(levels(as.factor(factor_months))[1]) %% 1 * 12,0)+1
print(paste("First month is",first_month))
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
write.table(total_df, "totalsim_on.csv", sep=",", row.names = FALSE, col.names = TRUE)
write.table(avg_values, "avgsim_on.csv", sep=",", row.names = FALSE, col.names = TRUE)



