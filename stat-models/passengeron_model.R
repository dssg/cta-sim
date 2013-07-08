### Required Libraries ###

library(rbugs)
library(coda)

### Loading and Cleaning the Data ###

stop_data = read.csv("/home/wdempsey/pickN21_stop831.csv", header = FALSE)  # Data For A Specific Stop During A Specific Quarter

names(stop_data) <- c("serial_number","survey_date","route_number", "time_actual_arrive","passengers_on","passengers_in","passengers_off")

summary(stop_data)

dim(stop_data)

actualtime <- function(x) { 
  ## Returns Time, Month, Year, and Day of Week from Date Column in Format "time_actual_arrive"

  date_time = strptime(x, format = "%m/%d/%Y %I:%M:%S %p")
  time = date_time$hour + date_time$min/60 + date_time$sec/(60*60)
  month = date_time$mon
  year = date_time$year
  wday = date_time$wday
  return(list(time = time, month = month, year = year, wday = wday))
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

buckets = buckets[,-c(1,74)] 
days = days[-c(1,74)]

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

emptyday = vector(length = num_days)  # Check to see if the Days are NonEmpty

for(i in 1:num_days) {emptyday[i] <- as.numeric(max(buckets[,i])==0)}


### BUGS CODE ###

# If Number of Months is less than 12, then we have a disconnect in dependency #

if(num_months < 12) {
model.str <- 'model
{
for (m in 1:num_buckets) {
  for (d in 1:num_days) {
    log(mu[m,d]) <- alpha[m]+beta[weekend[d]]+gamma[months[d]]
    Y[m, d] ~ dpois(phi[m, d])
    phi[m,d] <- exp(mu[m,d]) * group[m]
  }
  group[m] ~ dbern(p[m])
}

for (m in 1:num_buckets) {
p[m] ~ dunif(0,1)
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
    Y[m, d] ~ dpois(phi[m, d])
    phi[m,d] <- exp(mu[m,d]) * group[m]
  }
  group[m] ~ dbern(p[m])
}

for (m in 1:num_buckets) {
p[m] ~ dunif(0,1)
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

model.file = file("model_poisson.bug")
writeLines(model.str, model.file)
close(model.file)

Ydata = round(buckets,0)

data <- list("num_buckets" = num_buckets, "num_days" = num_days, "Y" = Ydata, "weekend" = weekend, "months" = months, "num_months" = num_months)

inits <- list(list(alpha0 = rnorm(1,0,0.01), alpha = replicate(num_buckets,rnorm(1,0,0.1)), itau2.alpha=rgamma(1, 0.1, 10),
                   beta0 = rnorm(1,0,0.01), beta = c(NA, rnorm(1,0,0.1)), itau2.beta = rgamma(1,0.1,10),
                   gamma0 = rnorm(1,0,0.01), gamma = replicate(num_months,rnorm(1,0,0.1)), itau2.gamma = rgamma(1, 0.1, 10),
                   p = replicate(num_buckets,runif(1))
))
parameters <- c("alpha0", "alpha", "itau2.alpha", "beta0", "beta", "itau2.beta", "gamma0", "gamma", "itau2.gamma", "p")

load.sim <- rbugs(data, inits, parameters, "model_poisson.bug",
                  verbose=T,
                  n.chains=1, n.iter=6000,
                  bugsWorkingDir="/tmp", cleanBugsWorkingDir = T)

load.mcmc <- rbugs2coda(load.sim)
summary(load.mcmc)
effectiveSize(load.mcmc)





