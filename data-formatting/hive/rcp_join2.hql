SET mapred.reduce.tasks=-1;
SET hive.exec.max.dynamic.partitions=100000;
SET hive.exec.max.dynamic.partitions.pernode=100000;

SET hive.exec.dynamic.partition = true;
SET hive.exec.dynamic.partition.mode = nonstrict;
-- (200 routes) * (2 directions) * (200 stops) = 80000;
-- (20 picks) * (200 routes) * (2 directions) * (200 stops)  = 1600000;
-- (100 buckets) * (16 picks) * (200 routes) * (200 stops) = 160000000;
SET hive.exec.max.dynamic.partitions.pernode = 100000;
SET hive.exec.max.dynamic.partitions = 5000000;
SET hive.exec.max.created.files = 500000000;

--SET mapred.max.split.size = 67108864;
--SET mapred.reduce.tasks = 19;
--SET hive.input.format=org.apache.hadoop.hive.ql.io.HiveInputFormat;

CREATE EXTERNAL TABLE rcp_detail (
 id STRING,
 serial_number STRING,
 sort_order STRING,
 signup_name STRING,
 stop_id STRING,
 main_cross_street STRING,
 travel_direction STRING,
 timepoint STRING,
 time_actual_arrive STRING,
 time_actual_depart STRING,
 serviced_stop STRING,
 next_day STRING,
 segment_miles STRING,
 passenger_miles STRING,
 passengers_on STRING,
 passengers_off STRING,
 passengers_in STRING,
 trip_early STRING,
 trip_ontime STRING,
 trip_late STRING,
 raw_fon STRING,
 raw_foff STRING,
 raw_ron STRING,
 raw_roff STRING,
 running_time_scheduled STRING,
 running_time_actual STRING,
 trip_diff_minutes STRING,
 time_scheduled STRING,
 timepoint_miles STRING
 )
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
LINES TERMINATED BY '\n'
--LOCATION 's3n://dssg-cta-data/rcptest/input/train/detail';
LOCATION '${INPUT}/detail';


CREATE EXTERNAL TABLE rcp_master (
 serial_number STRING,
 schedule_id STRING,
 schedule_name STRING,
 signup_name STRING,
 protected STRING,
 archived STRING,
 handheld_name STRING,
 handheld_number STRING,
 handheld_timestamp STRING,
 application_timestamp STRING,
 handheld_done STRING,
 distinct_trip STRING,
 survey_status STRING,
 survey_type STRING,
 survey_source STRING,
 pattern_id STRING,
 branch STRING,
 route_number STRING,
 route_name STRING,
 direction_name STRING,
 service_code STRING,
 service_type STRING,
 service_mode STRING,
 survey_date STRING,
 survey_date_effective STRING,
 survey_date_atypical STRING,
 trip_start_time STRING,
 trip_end_time STRING,
 next_day STRING,
 time_period STRING,
 service_day STRING,
 service_period STRING,
 trip_number STRING,
 trip_key STRING,
 block_number STRING,
 block_key STRING,
 run_number STRING,
 run_key STRING,
 operator_id STRING,
 vehicle_number STRING,
 vehicle_description STRING,
 vehicle_seats STRING,
 garage_id STRING,
 garage_name STRING,
 division_id STRING,
 division_name STRING,
 revenue_start STRING,
 revenue_end STRING,
 revenue_net STRING,
 odom_start STRING,
 odom_end STRING,
 odom_net STRING,
 condition_number STRING,
 checker_id STRING,
 checker_name STRING,
 revenue_miles STRING,
 revenue_hours STRING,
 actual_start_time STRING,
 actual_end_time STRING,
 total_passengers_on STRING,
 total_passengers_off STRING,
 total_passengers_in STRING,
 total_passenger_miles STRING,
 total_seat_miles STRING,
 max_load STRING,
 max_load_p STRING,
 pass_per_mile STRING,
 pass_per_hour STRING,
 tp_ontime STRING,
 tp_early STRING,
 tp_late STRING,
 ontime STRING,
 ontime_diff_avg STRING,
 dwell_time_avg STRING,
 scheduled_speed STRING,
 actual_speed STRING,
 total_stops STRING,
 requires_update STRING,
 last_update STRING,
 comments STRING,
 trip_count STRING,
 new_survey STRING,
 distinct_trip_avl STRING,
 total_wheelchairs STRING,
 pullin_time_scheduled STRING,
 pullin_time_actual STRING,
 pullout_time_scheduled STRING,
 pullout_time_actual STRING,
 trip_id STRING,
 total_demog_01 STRING,
 total_demog_02 STRING,
 total_demog_03 STRING,
 total_demog_04 STRING,
 total_demog_05 STRING,
 total_demog_06 STRING,
 total_demog_07 STRING,
 total_demog_08 STRING,
 total_demog_09 STRING,
 total_demog_10 STRING,
 total_demog_11 STRING,
 total_demog_12 STRING,
 total_demog_13 STRING,
 total_demog_14 STRING,
 total_demog_15 STRING,
 total_demog_16 STRING,
 total_demog_17 STRING,
 total_demog_18 STRING,
 total_demog_19 STRING,
 total_demog_20 STRING,
 total_demog_21 STRING,
 total_demog_22 STRING,
 total_demog_23 STRING,
 total_demog_24 STRING,
 total_demog_25 STRING,
 time_period_sort STRING,
 min_load STRING,
 farebox STRING,
 supervisor_attn STRING,
 non_student_fare STRING,
 match_count STRING,
 service_class STRING,
 start_load STRING,
 end_load STRING,
 total_fon STRING,
 total_foff STRING,
 total_ron STRING,
 total_roff STRING,
 type_avlcheck STRING,
 type_farecheck STRING,
 type_ridecheck STRING,
 trip_count_avlcheck STRING,
 trip_count_farecheck STRING,
 trip_count_ridecheck STRING,
 pattern_key STRING,
 total_demog_sum STRING,
 standing_area STRING,
 deadhead_minutes_scheduled STRING,
 layover_minutes_scheduled STRING,
 transaction_rollup_id STRING,
 total_demog_26 STRING,
 total_demog_27 STRING,
 checker_after_minutes STRING,
 checker_before_minutes STRING,
 temperature STRING,
 block_id STRING,
 type_odcheck STRING,
 assignment STRING,
 total_bicycles STRING,
 total_kneels STRING,
 actual_start_time_before STRING,
 actual_end_time_after STRING,
 start_timepoint STRING,
 end_timepoint STRING,
 onboard_software_version STRING,
 schedule_software_version STRING,
 load_duration STRING,
 load_duration_alt STRING,
 total_traffic_priority STRING,
 layover_minutes_actual STRING,
 deadhead_minutes_actual STRING,
 route_name_alt STRING
 ) 
ROW FORMAT DELIMITED FIELDS TERMINATED BY ',' 
LINES TERMINATED BY '\n' 
--LOCATION 's3n://dssg-cta-data/rcptest/input/train/master';
LOCATION '${INPUT}/master';

--CREATE EXTERNAL TABLE rcp_join2_apc (
-- serial_number STRING,
-- survey_date STRING,
-- pattern_id STRING,
-- time_actual_arrive STRING,
-- time_actual_depart STRING,
-- passengers_on STRING,
-- passengers_in STRING,
-- passengers_off STRING
-- )
--PARTITIONED BY(taroute STRING, direction_name STRING, stop_id STRING)
--ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
--LINES TERMINATED BY '\n'
--STORED AS TEXTFILE
----LOCATION 's3n://dssg-cta-data/rcp_join2/train/apc';
--LOCATION '${OUTPUT}/apc';
--
--INSERT OVERWRITE TABLE rcp_join2_apc
--PARTITION(taroute, direction_name, stop_id)
--SELECT
-- master.serial_number,
-- master.survey_date,
-- master.pattern_id,
-- substr(detail.time_actual_arrive,12),
-- substr(detail.time_actual_depart,12),
-- detail.passengers_on,
-- detail.passengers_in,
-- detail.passengers_off,
-- substr(master.route_name,8),
-- master.direction_name,
-- detail.stop_id
--FROM
--rcp_master master
--JOIN
--rcp_detail detail
--ON
--(master.serial_number = detail.serial_number)
--WHERE detail.timepoint = "0";

CREATE EXTERNAL TABLE rcp_join2_avl (
 serial_number STRING,
 survey_date STRING,
 pattern_id STRING,
 time_actual_arrive STRING,
 time_actual_depart STRING,
 trip_diff_minutes STRING,
 time_scheduled STRING
 )
PARTITIONED BY(taroute STRING, direction_name STRING, stop_id STRING)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
LINES TERMINATED BY '\n'
STORED AS TEXTFILE
LOCATION '${OUTPUT}/avl';

INSERT OVERWRITE TABLE rcp_join2_avl
PARTITION(taroute, direction_name, stop_id)
SELECT
 master.serial_number,
 master.survey_date,
 master.pattern_id,
 substr(detail.time_actual_arrive,12),
 substr(detail.time_actual_depart,12),
 detail.trip_diff_minutes,
 substr(detail.time_scheduled,12),
 substr(master.route_name,8),
 master.direction_name,
 detail.stop_id
FROM
rcp_master master
JOIN
rcp_detail detail
ON
(master.serial_number = detail.serial_number)
WHERE detail.timepoint = "-1";
