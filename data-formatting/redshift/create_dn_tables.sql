CREATE TABLE dn_bt_version AS
SELECT * FROM
(
  SELECT bt_ver,s_ver,distributiondate AS startdate, 
  lead(trunc(dateadd(day,-1,distributiondate)),1)
  OVER (ORDER BY distributiondate) AS enddate FROM bt_version
)
WHERE enddate IS NOT NULL ORDER BY startdate;

CREATE TABLE dn_day_version SORTKEY(mmddyyyy) AS
SELECT ld.mmddyyyy, ld.adate, dbv.bt_ver, dbv.s_ver
FROM lu_day AS ld join dn_bt_version AS dbv 
ON (ld.mmddyyyy BETWEEN dbv.startdate AND dbv.enddate);
UNLOAD ('SELECT * FROM dn_day_version')
TO 's3://cta-data/rcptest/tables/dn_day_version/'
CREDENTIALS 'aws_access_key_id=$[AWS_ACCESS_KEY_ID];aws_secret_access_key=$[AWS_SECRET_ACCESS_KEY]';

CREATE TABLE dn_bt_stop DISTKEY(bt_ver) SORTKEY(bt_ver,geoid) AS
SELECT bt_ver, geoid, geodescription, tageoid, longitude, latitude, heading,
lag(bt_ver,1) OVER (PARTITION BY geoid ORDER BY bt_ver) AS bt_ver_before,
lead(bt_ver,1) OVER (PARTITION BY geoid ORDER BY bt_ver) AS bt_ver_after
FROM bt_stop;

CREATE TABLE dn_bt_timepoint DISTKEY(bt_ver) SORTKEY(bt_ver,timepointid) AS
SELECT bt_ver, timepointid, placeid, description, longitude, latitude,
lag(bt_ver,1) OVER (PARTITION BY timepointid ORDER BY bt_ver) AS bt_ver_before,
lead(bt_ver,1) OVER (PARTITION BY timepointid ORDER BY bt_ver) AS bt_ver_after
FROM bt_timepoint;

CREATE TABLE dn_lu_ctrl_pt SORTKEY(taroute,dir_group) AS
SELECT lcp.taroute, ld.dir_group, lcp.direction_id, lcp.direction, lcp.placeid,
btt.bt_ver, btt.timepointid, btt.description, btt.latitude, btt.longitude
FROM lu_ctrl_pt lcp, bt_timepoint btt, lu_direction ld
WHERE btt.bt_ver=402 AND lcp.placeid=btt.placeid AND lcp.direction_id=ld.direction_id;

CREATE TABLE dn_bt_veh_max_capacity DISTKEY(bt_ver) SORTKEY(bt_ver,blockno) AS
SELECT ver.bt_ver, vt.blockno, CASE vt.veh_type WHEN '30ft' THEN 40
WHEN '40ft' THEN 70 WHEN '60ft' THEN 95 ELSE NULL END AS veh_max_capacity
FROM bt_version ver, bt_veh_type vt WHERE ver.s_ver = vt.s_ver;

