#!/bin/Rscript

# Type each of the following in the same line of the terminal:
# Rscript supply_gpmodel.R
# 6 0 1423

# This will run the code on data inside of the folder.

### Command Line Arguments ###

args <- commandArgs(TRUE)

input_taroute <- toString(args[1])
input_dir_group <- toString(args[2])

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

time_pt_data<-sqlQuery(conn,paste("select * from rcp_join_dn1_train_avl where taroute =",input_taroute,"and dir_group =",input_dir_group,"order by serial_number, time_sched\
uled;"))

names(time_pt_data) <- c("serial_number","survey_date","pattern_id", "time_actual_arrive","time_actual_depart", "trip_diff","time_scheduled","trip_diff_prev", "taroute", "dir_group","time_point_id")

summary(time_pt_data)

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

# time_pt_data$time <- actualtime(time_pt_data$time_actual_arrive)$time

# Fix trip_diff_minutes_prev 

time_pt_data$trip_diff_prev[time_pt_data$trip_diff_prev == ""] == 0.0

# Calculate Distance since Previous Time Point #

time_scheduled = actualtime(time_pt_data$time_scheduled)$time

time_diff = time_scheduled - c(0,time_scheduled[1:(length(time_scheduled)-1)])

time_diff[time_pt_data$trip_diff_prev == 0] = 0

### BUGS CODE - Estimate Single Variance Parameter ###

model.str <- 'model
{
  for(d in 1:num_obs) {
    delta[d] ~ dnorm(mu[d], var[d])
    mu[d] <- alpha0 + alpha1 * prev_delta[d] + alpha2 * w[d]
    var[d] <- itau2.alpha * diff_arrival[d] + itau2.gamma
    w[d] ~ dnorm(0, wvar[d]) I(0,)
    wvar[d] <- itau2.beta * diff_arrival[d]    
  }
  alpha0 ~ dnorm(0,.001)
  alpha1 ~ dnorm(0,.001)
  alpha2 ~ dnorm(0,.001)
  itau2.alpha ~ dgamma(1,1)
  itau2.beta ~ dgamma(1,1)
  itau2.gamma ~ dgamma(1,1)
}'

model.file = file("gaussianprocess_model.bug")
writeLines(model.str, model.file)
close(model.file)

data <- list("diff_arrival" = time_dist, "delta" = time_pt_data$trip_diff, "prev_delta" = time_pt_data$trip_diff_prev, "num_obs" = num_obs)

inits <- list(list(itau2.alpha = rgamma(1,0.1,10), alpha = rnorm(1,0,0.1)))

parameters <- c("itau2.alpha", "alpha")

load.sim <- jags(data, inits, parameters, "gaussianprocess_model.bug", n.chains=1, n.iter=6000, n.burnin=200, progress.bar="text")

print(load.sim)

load.mcmc <- as.mcmc(load.sim)

# Re-order month variables #

df_mcmc <- data.frame(load.mcmc)

avg_values = df_mcmc[1,]

for (i in 1:dim(total_df)[2]) {
    avg_values[,i] = mean(df_mcmc[,i])
}

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

#PARAMETERS
# values = as.vector(as.matrix(avg_values))
# 
# nTimeOfDay=list(values[1:48])
# names(nTimeOfDay)=c("llTimeOfDay")
# nDayType=list(values[49:50])
# names(nDayType)=c("llDayType")
# rhoTimeOfDay=list(values[51:97])
# names(rhoTimeOfDay)=c("rhoTimeOfDay")
# nMonth=list(values[98:109])
# names(nMonth)= c("llMonth")
# 
# param_list = c(nTimeOfDay, nDayType, rhoTimeOfDay, nMonth)
# 
# #TAGEOID
# cL=list(param_list)
# names(cL)=c(tageoid)
# 
# #DIR_GROUP
# bL=list(cL)
# names(bL)=c(dir_group)
# 
# #TAROUTE
# aL=list(bL)
# names(aL)=c(taroute)
# 
# #FIT DATE
# final=list(aL)
# names(final)=c(Sys.Date())
# 
# json = toJSON(final)
# 
# # setwd("cta-webapp/src/main/resources/")
# 
# print("Made it to Creating the JSON File")
# 
# print(paste("json_output/boardParams_",input_tageoid,".json",sep = ""))
# 
# write(json, file=paste("json_output/boardParams_",input_tageoid,".json",sep = ""), append = TRUE)
# 
# print("Created JSON File")
