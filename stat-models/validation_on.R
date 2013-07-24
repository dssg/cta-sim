# Turn Command Line Arguments On

# Command Line: Rscript validation_on.R /home/wdempsey/dssg-cta-project/stat-models/mcmc_output/avgsim_on.csv
#  	  	s3://dssg-cta-data/rcp_join2/test/apc/taroute=6/direction_name=North/stop_id=17076


args <- commandArgs(TRUE)
parameter_pathname <- toString(args[1]) # Model Parameters
datafile_pathname <- toString(args[2]) # Location of Test Data

### Loading and Cleaning the Data and Parameter Estimates ###

params = read.csv(parameter_pathname, header = TRUE)

test_data = read.csv(pipe(paste("bash ../util/catdir-s3.sh", datafile_pathname)), header = FALSE)

names(test_data) <- c("serial_number","survey_date","pattern_id", "time_actual_arrive",
		    "time_actual_depart", "passengers_on","passengers_in","passengers_off")

# summary(test_data)

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

test_data$time <- actualtime(test_data$time_actual_arrive)$time

### Remove NAs ###
if( length(which(is.na(test_data$time))) != 0) {
    test_data = test_data[-which(is.na(test_data$time)),]
}

# summary(test_data$time)

### Re-order Data To Compute Headways ###
date <- dateinfo(test_data$survey_date)
year <- date$year
month <- date$month
day <- date$day
time <- test_data$time
weekend <- as.numeric(date$wday == 0 | date$wday == 6) + 1
timeorder <- order(year,month, day, time) 

test_data = test_data[timeorder,]

total_obs = length(test_data$time)

test_data$headway = c(0,test_data$time[2:total_obs] - test_data$time[1:(total_obs-1)]) %% 24

### Construct Explanatory Variables for Given Observation ###

alpha = params[1:47]
beta = params[48:49]
gamma = params[50:61]

halfhr_bucket <- function(t) {
    halfhr = seq(0,24,0.5)
    difft = abs(halfhr - floor(t))
    min_diff = min(difft)
    return(which(difft == min_diff)[1])   
}


print("We made it to the MSE Calc")

MSE = 0

result = c(0,0)

for (k in 2:length(weekend)) {

    obs_halfhour = as.numeric(halfhr_bucket(time[k]))
    obs_month = as.numeric(month[k]+1)
    obs_weekend = as.numeric(weekend[k])
    
    halfhr_headway = test_data$headway[k]*2
    rate = as.numeric(alpha[obs_halfhour])+as.numeric(gamma[obs_month])+as.numeric(beta[obs_weekend])

    guess = exp(rate)*halfhr_headway
    truth = test_data$passengers_on[k]

    result = rbind(result,c(guess,truth))

    if( is.na(guess) == FALSE | is.na(truth) == FALSE) {
    	if (halfhr_headway < 2) {
            MSE = MSE + (guess - truth)^2
    	}
    }
}

norm_MSE = MSE / length(weekend)

print(result[-1,])

print(norm_MSE)

write.table(norm_MSE, "norm_MSE.csv", sep=",", row.names = FALSE, col.names = FALSE)
