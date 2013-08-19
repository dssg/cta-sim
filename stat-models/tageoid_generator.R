#!/bin/Rscript

args <- commandArgs(TRUE)

input_taroute <- toString(args[1])
input_dir_group <- toString(args[2])

print("Connecting through Redshift to Database")

library(RODBC)

conn <- odbcConnect("dssg_cta_redshift")

print(paste("select distinct tageoid from rcp_join_dn1_train_apc  where taroute =",input_taroute,"and dir_group =",input_dir_group,";"))

tageoids <- sqlQuery(conn, paste("select distinct tageoid from rcp_join_dn1_train_apc  where taroute =",input_taroute,"and dir_group =",input_dir_group,";"))

write.table(tageoids, file = "tageoids.csv", row.names = FALSE, col.names = FALSE)