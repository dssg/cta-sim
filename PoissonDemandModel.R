#
# Toy simulation, estimation and plotting for a poisson response model
# of passenger demand. 
#
#


#install.packages(c("rbugs", "glmmBUGS", "coda", "gridExtra"))
library(rbugs)
library(coda)
library(ggplot2)
library(plyr)
library(gridExtra)

# time span (half hours)
N <- 24
#
#  Generate simulations for observed load at each stop
#
M <- 20
true.params = data.frame(coefs=cbind(rnorm(M, 2, 0.01), 
                                     rnorm(M, 1, 0.05), 
                                     rnorm(M, 0, 0.07)), 
                         day=1:M)
omega1 <- 2*pi/2
omega2 <- 2*pi/10

F <- data.matrix(cbind(rep(1, N), sin(omega1 * (1:N)/N), cos(omega1 * (1:N)/N)))
Y <- ddply(true.params, .(day), function(params) {
           return(data.frame(halfHours=1:N, 
                             load=rpois(N, exp(F %*% t(as.vector(params[1:3])))),
                             tripsPerHr=c(rep(2, N/4), rep(10, N/4), 
                                          rep(5, N/4), rep(2, N/4)),
                             day=params$day))
           })

Y.plot.data = ddply(Y, .(day), transform, flow=load*tripsPerHr,
                    headway=pmin(30, 30/(load*tripsPerHr/55)))

load.plot <- ggplot(Y.plot.data, aes(x=factor(halfHours), y=load)) + stat_ydensity() 
load.plot <- load.plot + stat_summary(fun.y=function(x) quantile(x, probs=0.75), 
                            colour="black", ymin=0, ymax=0)
print(load.plot)

flow.plot <- ggplot(Y.plot.data, aes(x=factor(halfHours), y=flow)) + stat_ydensity() 
flow.plot <- flow.plot + stat_summary(fun.y=function(x) quantile(x, probs=0.75), 
                            colour="black", ymin=0, ymax=0)
print(flow.plot)

headway.plot <- ggplot(Y.plot.data, aes(x=factor(halfHours), y=headway)) + stat_ydensity() 
headway.plot <- headway.plot + stat_summary(fun.y=function(x) quantile(x, probs=0.75), 
                            colour="black", ymin=0, ymax=0)
print(headway.plot)

jpeg("flowLoadHeadway_example1.jpg")
grid.arrange(load.plot, flow.plot, headway.plot)
dev.off()

model.str <- 'model
{
  for (t in 1:N) {
    for (m in 1:M) {
      log(mu[t, m]) <- alpha + beta[1] * sin(2*3.14159/2 * t/N) + beta[2] * cos(2*3.14159/2 * t/N) 
      Y[t, m] ~ dpois(mu[t, m])
    }
  }
  itau2.alpha ~ dgamma(0.1, 10)
  itau2.beta[1] ~ dgamma(0.1, 10)
  itau2.beta[2] ~ dgamma(0.1, 10)
  lambda.beta[1] ~ dexp(2)
  lambda.beta[2] ~ dexp(2)
  icov.beta1 <- itau2.beta[1]/lambda.beta[1]
  icov.beta2 <- itau2.beta[2]/lambda.beta[2]
  alpha ~ dnorm(0, itau2.alpha)
  beta[1] ~ dnorm(0, icov.beta1)
  beta[2] ~ dnorm(0, icov.beta2)
}'


model.file = file("model.bug")
#file.show("model.bug")
writeLines(model.str, model.file)
close(model.file)


Ydata = data.matrix(unstack(Y, load ~ day))
data <- list("M" = M, "N" = N, "Y" = Ydata)
inits <- list(list(alpha=rnorm(1, 0, 0.1), itau2.alpha=rgamma(1, 0.1, 10),
                   beta=replicate(2, rnorm(1, 0, 0.1)), 
                   itau2.beta=replicate(2, rgamma(1, 0.1, 10)),
                   lambda.beta=replicate(2, rexp(1, 2))
                   ))
parameters <- c("alpha", "itau2.alpha", "beta", "itau2.beta", "lambda.beta")

load.sim <- rbugs(data, inits, parameters, "model.bug", 
                    verbose=F, 
                    n.chains=1, n.iter=2000, 
                    bugsWorkingDir="/tmp", cleanBugsWorkingDir = T)

load.mcmc <- rbugs2coda(load.sim)
summary(load.mcmc)
effectiveSize(load.mcmc)



