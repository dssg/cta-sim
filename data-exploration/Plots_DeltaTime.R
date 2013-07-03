library(ggplot2)

# Time difference from schedule ALL BUSES

my.data=read.table("/Users/Andres/Documents/ChicagoCTA/Data viz/BSHT/DeltaTimes.csv", header=T, sep=",")
colnames(my.data)[1]<-"DELTA_TIME"

my.data$DELTA_TIME<-my.data$DELTA_TIME/60

ontime_t<-rep("not on time",nrow(my.data)) 
ontime_t[my.data$DELTA_TIME>= -1 & my.data$DELTA_TIME<=5]="on time"
my.data$on_time<-ontime_t

base <- ggplot(data = my.data, mapping = aes(x = DELTA_TIME, fill = my.data$on_time)) + xlab("Time difference from schedule (seconds)") + ylab("Number of bus arrivals") + guides(fill=guide_legend(title=NULL)) + ggtitle('Deviation from schedule. March 2012')

base + geom_histogram(binwidth=1/60) + scale_x_continuous(limits=c(-10, 15))

#-------------------------------------------------------------------------------
# Time difference from schedule 4 ROUTES

my.data=read.table("/Users/Andres/Documents/ChicagoCTA/Data viz/BSHT/DeltaTimes_Routes.csv", header=F, sep=",")
colnames(my.data)[1]<-"ROUTE_ID"
colnames(my.data)[2]<-"DELTA_TIME"

my.data$DELTA_TIME<-my.data$DELTA_TIME/60

ontime_t<-rep("not on time",nrow(my.data)) 
ontime_t[my.data$DELTA_TIME>= -1 & my.data$DELTA_TIME<=5]="on time"
my.data$on_time<-ontime_t

base3 <- ggplot(data = my.data, mapping = aes(x= DELTA_TIME, fill = on_time)) + xlab("Time difference from schedule (seconds)") + ylab("Number of bus arrivals") + guides(fill=guide_legend(title=NULL)) + ggtitle('Deviation from schedule. March 2012') 
base3 + geom_histogram(binwidth=1/60) + scale_x_continuous(limits=c(-10, 15)) + scale_y_continuous(limits=c(0, 400)) + facet_wrap(~ROUTE_ID, ncol=2) 

#--------------------
#One more try

my.data=read.table("/Users/Andres/Documents/ChicagoCTA/Data viz/BSHT/DeltaTimes_Route6.csv", header=F, sep=",")
colnames(my.data)[1]<-"ROUTE_ID"
colnames(my.data)[2]<-"DELTA_TIME"

my.data$DELTA_TIME<-my.data$DELTA_TIME/60

ontime_t<-rep("not on time",nrow(my.data)) 
ontime_t[my.data$DELTA_TIME>= -1 & my.data$DELTA_TIME<=5]="on time"
my.data$on_time<-ontime_t

base2 <- ggplot(data = my.data, mapping = aes(x= DELTA_TIME, fill = on_time)) + xlab("Time difference from schedule (seconds)") + ylab("Number of bus arrivals") + guides(fill=guide_legend(title=NULL)) + ggtitle('Deviation from schedule. March 2012') + geom_histogram(binwidth=1/60) + scale_x_continuous(limits=c(-10, 15)) 

my.data2=read.table("/Users/Andres/Documents/ChicagoCTA/Data viz/BSHT/DeltaTimes_Route9.csv", header=F, sep=",")
colnames(my.data2)[1]<-"ROUTE_ID"
colnames(my.data2)[2]<-"DELTA_TIME"

my.data2$DELTA_TIME<-my.data2$DELTA_TIME/60

ontime_t<-rep("not on time",nrow(my.data2)) 
ontime_t[my.data2$DELTA_TIME>= -1 & my.data2$DELTA_TIME<=5]="on time"
my.data2$on_time<-ontime_t

base3 <- ggplot(data = my.data2, mapping = aes(x= DELTA_TIME, fill = on_time)) + xlab("Time difference from schedule (seconds)") + ylab("Number of bus arrivals") + guides(fill=guide_legend(title=NULL)) + ggtitle('Deviation from schedule. March 2012') + geom_histogram(binwidth=1/60) + scale_x_continuous(limits=c(-10, 15))

multiplot <- function(..., plotlist=NULL, file, cols=1, layout=NULL) {
  require(grid)

  # Make a list from the ... arguments and plotlist
  plots <- c(list(...), plotlist)

  numPlots = length(plots)

  # If layout is NULL, then use 'cols' to determine layout
  if (is.null(layout)) {
    # Make the panel
    # ncol: Number of columns of plots
    # nrow: Number of rows needed, calculated from # of cols
    layout <- matrix(seq(1, cols * ceiling(numPlots/cols)),
                    ncol = cols, nrow = ceiling(numPlots/cols))
  }

 if (numPlots==1) {
    print(plots[[1]])

  } else {
    # Set up the page
    grid.newpage()
    pushViewport(viewport(layout = grid.layout(nrow(layout), ncol(layout))))

    # Make each plot, in the correct location
    for (i in 1:numPlots) {
      # Get the i,j matrix positions of the regions that contain this subplot
      matchidx <- as.data.frame(which(layout == i, arr.ind = TRUE))

      print(plots[[i]], vp = viewport(layout.pos.row = matchidx$row,
                                      layout.pos.col = matchidx$col))
    }
  }
}

multiplot(base2, base3)

#---------------------------------------------------------------------------------



