library(ggplot2)


#data=read.csv("/Users/Andres/Documents/ChicagoCTA/dssg-cta-project/cta-example-data/bus_state_hist_sample.csv")
#my.data=read.csv("/Users/Andres/Documents/ChicagoCTA/dssg-cta-project/cta-example-data/bus_state_hist_timpt_sample.csv")
#stop_data=read.csv("/Users/Andres/Documents/ChicagoCTA/dssg-cta-project/cta-example-data/ride_check_master_sample.csv")
#schedule=read.csv("/Users/Andres/Documents/ChicagoCTA/dssg-cta-project/cta-example-data/schd_bus_timepoint_times_sample.csv")
my.data=read.table("/Users/Andres/Desktop/BSHT2013MAR.txt", header=F, sep="," )
#my.columns=read.table(t)
#my.data= scan("/Users/Andres/Desktop/BSHT2013MAR.txt"what=list())

# See what the data look like
#head(my.data)
#str(my.data)

# Only for full data set
colnames(my.data)[5]<-"ROUTE_ID"
colnames(my.data)[13]<-"DELTA_TIME"

#Add the on time column
ontime_t<-rep("not on time",nrow(my.data)) 
ontime_t[my.data$DELTA_TIME>= -60 & my.data$DELTA_TIME<=300]="on time"

my.data$on_time<-ontime_t
head(my.data)

# Check for any errors
#check<-cbind(my.data$DELTA_TIME, ontime_t)
#head(check)

# Try different ways of visualizing
columns<-floor(nlevels(my.data$ROUTE_ID)/5)

base <- ggplot(data = my.data, mapping = aes(x = DELTA_TIME)) + xlab("Arrival time DELTA")
# Full distribution
base +  geom_histogram(aes(y = ..density..), binwidth=5, fill="sky blue") +
  geom_density(colour="black") + scale_x_continuous(limits=c(-300, 700))
base +  geom_histogram(aes(y = ..density..), binwidth=60, fill="sky blue") + 
  geom_density(colour="black") + scale_x_continuous(limits=c(-300, 700)) + facet_wrap(~ROUTE_ID, ncol=columns, scales="free") 
base +  geom_density(colour="black") + scale_x_continuous(limits=c(-300, 700)) 
base +  geom_density(fill="black") + scale_x_continuous(limits=c(-300, 700)) + 
  facet_wrap(~ROUTE_ID, ncol=columns, scales="free")

base2 <- ggplot(data = my.data, mapping = aes(x = DELTA_TIME, fill = my.data$on_time)) + xlab("Arrival time DELTA") + guides(fill=guide_legend(title=NULL))
# On time and not on time distributions
base2 + geom_histogram(binwidth=5, aes(y = ..density..)) + geom_density(alpha = .1,colour="black") + scale_x_continuous(limits=c(-300, 700))
#base2 + geom_histogram(binwidth=10)  + scale_x_continuous(limits=c(-300, 700)) + facet_wrap(~ROUTE_ID, ncol=columns, scales="free")
base2 + geom_density(alpha = .5) + scale_x_continuous(limits=c(-300, 700)) 
#base2 + geom_density(alpha = .5) + scale_x_continuous(limits=c(-300, 700)) + facet_wrap(~ROUTE_ID, ncol=columns, scales="free")

# coloring for each route
base3 <- ggplot(data = my.data, mapping = aes(x = DELTA_TIME, fill = factor(ROUTE_ID))) + xlab("Arrival time DELTA")
base3 + geom_density() + scale_x_continuous(limits=c(-300, 700)) 
base3 + geom_density() + scale_x_continuous(limits=c(-300, 700)) + facet_wrap(~ROUTE_ID, ncol=columns) + guides(fill=FALSE)