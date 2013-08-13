CREATE TABLE bus_state_hist_timept
(
 bus_state_id         NUMERIC,
 event_time           TIMESTAMP,
 bus_id               NUMERIC(5),
 bustools_ver_id      NUMERIC(4),
 route_id             VARCHAR(5),
 pattern              VARCHAR(2),
 stop_sequence        NUMERIC(3),
 operator_id          NUMERIC(10),
 run_id               VARCHAR(5),
 odometer_distance    NUMERIC,
 route_status         NUMERIC(2),
 dwell_time           NUMERIC(16),
 delta_time           NUMERIC(16),
 latitude             FLOAT4,
 longitude            FLOAT4,
 heading              FLOAT4,
 nav_state            NUMERIC(1),
 block_id             NUMERIC(8),
 work_version         VARCHAR(8),
 work_status          VARCHAR(2),
 trip_id              NUMERIC(8),
 trip_type            NUMERIC(1),
 departure_time       TIMESTAMP,
 trip_start_time      TIMESTAMP,
 survey_date          DATE,
 timepoint_id         VARCHAR(8),
 timepoint_type       NUMERIC(2),
 timepoint_dwell      NUMERIC(16),
 timepoint_status     NUMERIC(2),
 schedule_mode        NUMERIC(1),
 PRIMARY KEY(bus_state_id)
)
DISTKEY(trip_id)
SORTKEY(survey_date,trip_id);

COPY bus_state_hist_timept FROM 's3://cta-data/bsht_uc' 
CREDENTIALS 'aws_access_key_id=$[AWS_ACCESS_KEY_ID];aws_secret_access_key=$[AWS_SECRET_ACCESS_KEY]'
DELIMITER AS ','
EMPTYASNULL
TIMEFORMAT AS 'MM-DD-YYYY HH24:MI:SS'
DATEFORMAT AS 'DD-MON-YY';
