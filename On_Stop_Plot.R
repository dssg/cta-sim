library(ggplot2)
library(gridExtra)

# Function to convert time to military time
convert.to.military = function(input.factor){
  if(input.factor!=""){
    if(nchar(as.character(input.factor))<11)
      input.factor=paste(0,input.factor,sep="")
    temp1 = as.numeric(substr(input.factor, 1, 2))
    temp2 = as.numeric(substr(input.factor, 4, 5))
    #temp3= as.numeric(substr(input.factor, 7, 8))/60
    
    if (temp1 == 12) temp1 = 0
    if(temp2!=99){
      if(temp2>45) {temp2=100}
      else if(temp2<15) {temp2 = 0}
      else {temp2=30}
    }
    
    time = temp1*100 + temp2 #+ temp3
    if(substr(input.factor, 10, 11) == "PM"){
      time = 1200 + (temp1*100) + temp2 #+ temp3
    }
  }
  else time=3000
  
  return(time)
}

# General Information
dataLocation="/Users/Andres/Documents/ChicagoCTA/Data viz/APC"
Quarter="N-23"
FileType="On_Time_Stop"
BusStop=9
dataType= ".csv"

# Time window in military time
timeMin=0100
timeMax=2330
hour=1800

Loc=paste(dataLocation,Quarter,sep="/")
Loc=paste(Loc,FileType, sep="/")
Loc=paste(Loc,BusStop, sep="")
Loc=paste(Loc,dataType, sep="")

my.data=read.table(Loc, header=F, sep=",")
colnames(my.data)[c(1:3)]<-c( "STOP_ID", "PASSENGER_ON", "TIME")

#Orders the data and retains only the data for the bus stop
sorted.data<-my.data[order(my.data$STOP_ID),]
sorted.data<-sorted.data[sorted.data$STOP_ID==BusStop,]

# Cuts the date out of the time
sorted.data$TIME=substr(sorted.data$TIME, 12, 22)

# Converts all data to military time
time=rep(0,nrow(sorted.data))
for(i in 1:length(sorted.data$TIME)){
  time[i] = convert.to.military(sorted.data$TIME[i])
  #print(i)
}

# Rounds the number to 6 digits
time=signif(time,6)
# Changes the TIME data
sorted.data$TIME=time
final.data=sorted.data[c("TIME","PASSENGER_ON")]

# Initial and final hours for the plot
final.data<-final.data[final.data$TIME>=timeMin,]
final.data<-final.data[final.data$TIME<=timeMax,]

# Poin plot for different hours
base2 <- ggplot(data = final.data, mapping = aes(x = factor(TIME), y=PASSENGER_ON)) + xlab("Hour of the day") + ylab("Number of people BOARDING") + ggtitle(paste('Number of people BORADING at Stop ',BusStop, sep=""))
base2 <- base2 + geom_point(position = 'jitter') + theme(panel.grid.minor = element_blank())
print(base2)

# Violin plots for different hours
violin.plot <- ggplot(final.data, aes(x=factor(TIME), y=PASSENGER_ON))  + stat_ydensity()
violin.plot<- violin.plot + stat_summary(fun.y=function(x) mean(x), 
                                         colour="red", ymin=0, ymax=0) 
violin.plot<- violin.plot + stat_summary(fun.y=function(x) quantile(x, prob=.75), 
                                         colour="black", ymin=0, ymax=0)+ xlab("Hour of the day") +
                                          scale_y_continuous(breaks = c(0:20)) +
                                          ylab("Number of people BOARDING") + ggtitle(paste('Number of people BORADING at Stop ',BusStop, sep=""))
print(violin.plot)

pdf(paste(paste("People_On_per_Hour_Stop", BusStop, sep=""),".pdf", sep=""),width=16, height=9)
grid.arrange(base2)
dev.off()
pdf(paste(paste("People_On_per_Hour_Violin_Stop", BusStop, sep=""),".pdf", sep=""),width=16, height=9)
grid.arrange(violin.plot)
dev.off()


hist.data=final.data[final.data$TIME==hour,]
hist.plot<- ggplot(data=hist.data, mapping=aes(x=PASSENGER_ON))+ xlab("Number of BORADING") + ylab("Count") + ggtitle(paste(paste(paste('Number of people BOARDING at Hour ',hour, sep=""), "at Stop"), BusStop))
hist.plot<-hist.plot + geom_histogram(binwidt=1) + theme(panel.grid.minor = element_blank())
print(hist.plot)

pdf(paste(paste(paste(paste("On_Hist", hour),"_Stop"), BusStop),".pdf"))
grid.arrange(hist.plot)
dev.off()
