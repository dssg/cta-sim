CREATE TABLE rcp_detail
(
 id                     NUMERIC(11) not null,
 serial_number          NUMERIC(11) not null,
 sort_order             NUMERIC(11) not null,
 signup_name            VARCHAR(50),
 stop_id                NUMERIC(11) default 99999,
 main_cross_street      VARCHAR(101),
 travel_direction       VARCHAR(2),
 timepoint              NUMERIC(1) default 0,
 time_actual_arrive     TIMESTAMP,
 time_actual_depart     TIMESTAMP,
 serviced_stop          NUMERIC(11),
 next_day               NUMERIC(1) default 0,
 segment_miles          FLOAT4,
 passenger_miles        FLOAT4,
 passengers_on          FLOAT4,
 passengers_off         FLOAT4,
 passengers_in          FLOAT4,
 trip_early             NUMERIC(3) default 0,
 trip_ontime            NUMERIC(3) default 0,
 trip_late              NUMERIC(3) default 0,
 fon                    NUMERIC(11),
 foff                   NUMERIC(11),
 ron                    NUMERIC(11),
 roff                   NUMERIC(11),
 running_time_scheduled FLOAT4,
 running_time_actual    FLOAT4,
 trip_diff_minutes      FLOAT4,
 time_scheduled         TIMESTAMP,
 timepoint_miles        FLOAT4,
 PRIMARY KEY(id)
)
DISTKEY(serial_number)
SORTKEY(serial_number,sort_order);

COPY rcp_detail FROM 's3://cta-data/rcp_uc2/detail'
CREDENTIALS 'aws_access_key_id=$[AWS_ACCESS_KEY_ID];aws_secret_access_key=$[AWS_SECRET_ACCESS_KEY]'
DELIMITER AS ','
EMPTYASNULL
TIMEFORMAT AS 'MM/DD/YYYY HH12:MI:SS PM';

