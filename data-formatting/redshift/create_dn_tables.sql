create table dn_bt_version as select * from (select bt_ver,s_ver,distributiondate as startdate, lead(trunc(dateadd(day,-1,distributiondate)),1) over (order by distributiondate) as enddate from bt_version) where enddate is not null order by startdate;

create table dn_day_version sortkey(mmddyyyy) as select ld.mmddyyyy, ld.adate, dbv.bt_ver, dbv.s_ver from lu_day as ld join dn_bt_version as dbv on (ld.mmddyyyy between dbv.startdate and dbv.enddate);
unload ('select * from dn_day_version') to 's3://cta-data/rcptest/tables/dn_day_version/' credentials 'aws_access_key_id=$[AWS_ACCESS_KEY_ID];aws_secret_access_key=$[AWS_SECRET_ACCESS_KEY]';

create table dn_bt_stop distkey(bt_ver) sortkey(bt_ver,geoid) as select bt_ver, geoid, geodescription, tageoid, longitude, latitude, heading, lag(bt_ver,1) over (partition by geoid order by bt_ver) as bt_ver_before, lead(bt_ver,1) over (partition by geoid order by bt_ver) as bt_ver_after from bt_stop;

create table dn_bt_timepoint distkey(bt_ver) sortkey(bt_ver,timepointid) as select bt_ver, timepointid, placeid, description, longitude, latitude, lag(bt_ver,1) over (partition by timepointid order by bt_ver) as bt_ver_before, lead(bt_ver,1) over (partition by timepointid order by bt_ver) as bt_ver_after from bt_timepoint;

create table dn_lu_ctrl_pt sortkey(taroute,dir_group) as select lcp.taroute,ld.dir_group,lcp.direction_id,lcp.direction,lcp.placeid,btt.bt_ver,btt.timepointid,btt.description,btt.latitude,btt.longitude from lu_ctrl_pt lcp, bt_timepoint btt, lu_direction ld where btt.bt_ver=402 and lcp.placeid=btt.placeid and lcp.direction_id=ld.direction_id;

create table rcp_join_dn1_train_avl_prev distkey(serial_number) sortkey(taroute,dir_group,time_scheduled) as select serial_number,survey_date,pattern_id,time_actual_arrive,time_actual_depart,trip_diff_minutes,time_scheduled,lag(trip_diff_minutes,1) over (partition by serial_number order by time_scheduled) as trip_diff_minutes_prev,taroute,dir_group,timepointid from rcp_join_dn1_train_avl;
