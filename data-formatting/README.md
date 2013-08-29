The bsht and rcp directories contain scripts to clean up the bus state hist timepoint and ridecheck plus data that we received from the CTA.

The redshift directory contains the SQL scripts used to create tables, import the data, and do some denormalization.

The hive directory contains Hive scripts.  We used Hive to do the join of the ridecheck plus master and detail tables before switching to Redshift.

The cta-hadoop directory contains a Hadoop script to partition data into separate files by stop.
