### Required Libraries ###
library(rbugs)
library(coda)

### Simulated Delta Times for the Fake Bus Route with 20 Evenly Spaced Stops ###

time_dist <- rep(1,20) # Distance in Time Between Stops
num_stops = length(time_dist)+1 # Number of Stops

exp_arr_time <- rep(0,length(time_dist)) # Expected Arrival Time at Stop
for(i in 2:num_stops){
  exp_arr_time[i] <- exp_arr_time[i-1] + time_dist[i-1]  
}
  
  
simulate <- function(time_dist, sigma) { # Simulate Delta Times for a Given Time_Dist and Sigma
  num_stops = length(time_dist)+1
  bus_times <- rep(0,num_stops) # Assume Delta Times are Conditional Normal, will adjust to be skewed normal
  for(i in 2:num_stops) {
    neg_portion <- rnorm(1,bus_times[i-1],sigma * time_dist[i-1])
    bus_times[i] <- rnorm(1,bus_times[i-1],sigma * time_dist[i-1]) + neg_portion * as.numeric(neg_portion < 0) 
  }
  return(bus_times)
}

sigma <- 0.5
1/sigma
num_obs <- 1000

simulation <- matrix(nrow = length(time_dist), ncol = num_obs)
for (i in 1:num_obs) {
  simulation[,i] <- simulate(time_dist,sigma)[2:num_stops]
}


### BUGS CODE - Estimate Single Variance Parameter ###

model.str <- 'model
{
  for(d in 1:num_obs) {
    delta[1,d] ~ dnorm(mu[1,d], var[1,d])
    mu[1,d] <- alpha * w[1,d]
    var[1,d] <- itau2.alpha / diff_arrival[1]
    w[1,d] ~ dnorm(0, wvar[1,d]) I(0,)
    wvar[1,d] <- itau2.alpha / diff_arrival[1]
    for(i in 2:(num_stops-1)) {
      delta[i,d] ~ dnorm( mu[i,d], var[i,d])
      mu[i,d] <- delta[i-1,d] + alpha * w[i,d]
      var[i,d] <- itau2.alpha / diff_arrival[i]
      w[i,d] ~ dnorm(0, wvar[i,d]) I(0,)
      wvar[i,d] <- itau2.alpha / diff_arrival[i]
    }
  }
  alpha ~ dnorm(0,.001)
  itau2.alpha ~ dgamma(1,1)
}'

model.file = file("gaussianprocess_model.bug")
writeLines(model.str, model.file)
close(model.file)

data <- list("diff_arrival" = time_dist, "delta" = simulation, "num_obs" = num_obs, "num_stops" = num_stops)

inits <- list(list(itau2.alpha = rgamma(1,0.1,10), alpha = rnorm(1,0,0.1)))

parameters <- c("itau2.alpha", "alpha")

load.sim <- rbugs(data, inits, parameters, "gaussianprocess_model.bug",
                  verbose=T,
                  n.chains=1, n.iter=6000, n.burnin = 1000, n.thin = 10,
                  bugsWorkingDir="/tmp", cleanBugsWorkingDir = T)

load.mcmc <- rbugs2coda(load.sim)
summary(load.mcmc)
effectiveSize(load.mcmc)


    
