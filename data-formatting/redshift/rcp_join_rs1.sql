CREATE TABLE rcp_join_dn1
DISTKEY(serial_number)
SORTKEY(serial_number,sort_order)
AS
SELECT
 master.serial_number,
 master.signup_name, 
 master.pattern_id,
 master.branch,
 master.route_name,
 master.direction_name,
 master.survey_date,
 master.survey_date_effective,
 master.trip_start_time,
 master.trip_end_time,
 master.time_period,
 master.service_day,
 master.service_period,
 master.trip_key,
 master.block_number,
 master.vehicle_seats,
 detail.sort_order,
 detail.stop_id,
 detail.main_cross_street,
 detail.timepoint,
 detail.time_actual_arrive,
 detail.time_actual_depart,
 detail.serviced_stop,
 detail.segment_miles,
 detail.passengers_on,
 detail.passengers_in,
 detail.passengers_off,
 detail.trip_early,
 detail.trip_ontime,
 detail.trip_late,
 detail.fon,
 detail.foff,
 detail.ron,
 detail.roff,
 detail.running_time_scheduled,
 detail.running_time_actual,
 detail.trip_diff_minutes,
 detail.time_scheduled,
 detail.timepoint_miles
FROM
rcp_master master
JOIN
rcp_detail detail
ON
(master.serial_number = detail.serial_number);


ALTER TABLE rcp_join_dn1 ADD COLUMN bt_ver NUMERIC(3);
ALTER TABLE rcp_join_dn1 ADD COLUMN s_ver NUMERIC(2);
UPDATE rcp_join_dn1 SET bt_ver = ddv.bt_ver, s_ver = ddv.s_ver FROM dn_day_version ddv WHERE survey_date = ddv.mmddyyyy;

ALTER TABLE rcp_join_dn1 ADD COLUMN tageoid VARCHAR(6);
UPDATE rcp_join_dn1 SET tageoid = dbs.tageoid FROM rcp_join_dn1 rcp, dn_bt_stop dbs WHERE rcp.timepoint=0 AND (rcp.stop_id = dbs.geoid AND (rcp.bt_ver > dbs.bt_ver_before OR dbs.bt_ver_before IS NULL) AND (rcp.bt_ver < dbs.bt_ver_after OR dbs.bt_ver_after IS NULL));

ALTER TABLE rcp_join_dn1 ADD COLUMN dir_group NUMERIC(1);
UPDATE rcp_join_dn1 SET dir_group = lud.dir_group FROM rcp_join_dn1 rcp, lu_direction lud WHERE rcp.direction_name = lud.direction;

ALTER TABLE rcp_join_dn1 ADD COLUMN timepointid NUMERIC(4);
UPDATE rcp_join_dn1 SET timepointid = dbt.timepointid FROM rcp_join_dn1 rcp, dn_bt_timepoint dbt WHERE rcp.timepoint=-1 AND (SUBSTRING(rcp.stop_id,4)::NUMERIC = dbt.timepointid AND (rcp.bt_ver > dbt.bt_ver_before OR dbt.bt_ver_before IS NULL) AND (rcp.bt_ver < dbt.bt_ver_after OR dbt.bt_ver_after IS NULL));

ALTER TABLE rcp_join_dn1 ADD COLUMN longitude FLOAT;
UPDATE rcp_join_dn1 SET longitude = dbs.longitude FROM rcp_join_dn1 rcp, dn_bt_stop dbs WHERE rcp.timepoint=0 AND (rcp.stop_id = dbs.geoid AND (rcp.bt_ver > dbs.bt_ver_before OR dbs.bt_ver_before IS NULL) AND (rcp.bt_ver < dbs.bt_ver_after OR dbs.bt_ver_after IS NULL));
UPDATE rcp_join_dn1 SET longitude = dbt.longitude FROM rcp_join_dn1 rcp, dn_bt_timepoint dbt WHERE rcp.timepoint=-1 AND (rcp.timepointid = dbt.timepointid AND (rcp.bt_ver > dbt.bt_ver_before OR dbt.bt_ver_before IS NULL) AND (rcp.bt_ver < dbt.bt_ver_after OR dbt.bt_ver_after IS NULL));

ALTER TABLE rcp_join_dn1 ADD COLUMN latitude FLOAT;
UPDATE rcp_join_dn1 SET latitude = dbs.latitude FROM rcp_join_dn1 rcp, dn_bt_stop dbs WHERE rcp.timepoint=0 AND (rcp.stop_id = dbs.geoid AND (rcp.bt_ver > dbs.bt_ver_before OR dbs.bt_ver_before IS NULL) AND (rcp.bt_ver < dbs.bt_ver_after OR dbs.bt_ver_after IS NULL));
UPDATE rcp_join_dn1 SET latitude = dbt.latitude FROM rcp_join_dn1 rcp, dn_bt_timepoint dbt WHERE rcp.timepoint=-1 AND (rcp.timepointid = dbt.timepointid AND (rcp.bt_ver > dbt.bt_ver_before OR dbt.bt_ver_before IS NULL) AND (rcp.bt_ver < dbt.bt_ver_after OR dbt.bt_ver_after IS NULL));


CREATE TABLE rcp_join_dn1_train_apc DISTKEY(serial_number) SORTKEY(taroute,dir_group,tageoid) AS SELECT serial_number,survey_date,pattern_id,SUBSTRING(time_actual_arrive,12) AS time_actual_arrive,SUBSTRING(time_actual_depart,12) AS time_actual_depart,passengers_on,passengers_in,passengers_off,SUBSTRING(route_name,8) AS taroute,dir_group,tageoid FROM rcp_join_dn1 WHERE timepoint=0 AND (signup_name='N-21' OR signup_name='N-22');

UNLOAD ('SELECT * from rcp_join_dn1_train_apc')
TO 's3://cta-data/rcp_join_rs1/train/apc/'
CREDENTIALS 'aws_access_key_id=$[AWS_ACCESS_KEY_ID];aws_secret_access_key=$[AWS_SECRET_ACCESS_KEY]'
DELIMITER AS ',';


CREATE TABLE rcp_join_dn1_train_avl DISTKEY(serial_number) SORTKEY(taroute,dir_group,timepointid) AS SELECT serial_number,survey_date,pattern_id,SUBSTRING(time_actual_arrive,12) AS time_actual_arrive,SUBSTRING(time_actual_depart,12) AS time_actual_depart,trip_diff_minutes,SUBSTRING(time_scheduled,12) AS time_scheduled,SUBSTRING(route_name,8) AS taroute,dir_group,SUBSTRING(stop_id,4) AS timepointid FROM rcp_join_dn1 WHERE timepoint=-1 AND (signup_name='N-21' OR signup_name='N-22');

UNLOAD ('SELECT * FROM rcp_join_dn1_train_avl')
TO 's3://cta-data/rcp_join_rs1/train/avl/'
CREDENTIALS 'aws_access_key_id=$[AWS_ACCESS_KEY_ID];aws_secret_access_key=$[AWS_SECRET_ACCESS_KEY]'
DELIMITER AS ',';


CREATE TABLE rcp_join_dn1_test_apc DISTKEY(serial_number) SORTKEY(taroute,dir_group,tageoid) AS SELECT serial_number,survey_date,pattern_id,SUBSTRING(time_actual_arrive,12) AS time_actual_arrive,SUBSTRING(time_actual_depart,12) AS time_actual_depart,passengers_on,passengers_in,passengers_off,SUBSTRING(route_name,8) AS taroute,dir_group,tageoid FROM rcp_join_dn1 WHERE timepoint=0 AND (signup_name='N-25' OR signup_name='N-26');

UNLOAD ('SELECT * FROM rcp_join_dn1_test_apc')
TO 's3://cta-data/rcp_join_rs1/test/apc/'
CREDENTIALS 'aws_access_key_id=$[AWS_ACCESS_KEY_ID];aws_secret_access_key=$[AWS_SECRET_ACCESS_KEY]'
DELIMITER AS ',';


CREATE TABLE rcp_join_dn1_test_avl DISTKEY(serial_number) SORTKEY(taroute,dir_group,timepointid) AS SELECT serial_number,survey_date,pattern_id,SUBSTRING(time_actual_arrive,12) AS time_actual_arrive,SUBSTRING(time_actual_depart,12) AS time_actual_depart,trip_diff_minutes,SUBSTRING(time_scheduled,12) AS time_scheduled,SUBSTRING(route_name,8) AS taroute,dir_group,SUBSTRING(stop_id,4) AS timepointid FROM rcp_join_dn1 WHERE timepoint=-1 AND (signup_name='N-25' OR signup_name='N-26');

UNLOAD ('SELECT * FROM rcp_join_dn1_test_avl')
TO 's3://cta-data/rcp_join_rs1/test/avl/'
CREDENTIALS 'aws_access_key_id=$[AWS_ACCESS_KEY_ID];aws_secret_access_key=$[AWS_SECRET_ACCESS_KEY]'
DELIMITER AS ',';


CREATE TABLE rcp_join_dn1_train_avl_prev DISTKEY(serial_number) SORTKEY(taroute,dir_group,time_scheduled) AS SELECT serial_number,survey_date,pattern_id,time_actual_arrive,time_actual_depart,trip_diff_minutes,time_scheduled,lag(trip_diff_minutes,1) OVER (partition BY serial_number ORDER BY time_scheduled) AS trip_diff_minutes_prev,taroute,dir_group,timepointid FROM rcp_join_dn1_train_avl;
