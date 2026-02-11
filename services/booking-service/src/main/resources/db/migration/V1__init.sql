-- BOOKINGS
create table if not exists bookings (
                                        booking_id varchar(64) primary key,
    shop_id varchar(64) not null,
    barber_id varchar(64) not null,
    customer_id varchar(64) not null,
    booking_date date not null,
    start_time time not null,
    end_time time not null,
    duration_minutes int not null,
    service_code varchar(64) not null,
    status varchar(32) not null,
    created_at timestamptz not null,
    updated_at timestamptz null
    );

create index if not exists idx_bookings_shop_barber_date_start
    on bookings (shop_id, barber_id, booking_date, start_time);

create index if not exists idx_bookings_shop_customer_date
    on bookings (shop_id, customer_id, booking_date);

-- IDEMPOTENCY (Step 3 uses this)
create table if not exists idempotency_keys (
                                                shop_id varchar(64) not null,
    idempotency_key varchar(128) not null,
    request_hash varchar(128) not null,
    response_json text null,
    status varchar(32) not null,
    created_at timestamptz not null,
    primary key (shop_id, idempotency_key)
    );

-- OUTBOX (Step 4 uses this)
create table if not exists outbox (
                                      id bigserial primary key,
                                      event_id varchar(64) not null,
    aggregate_type varchar(64) not null,
    aggregate_id varchar(64) not null,
    event_type varchar(128) not null,
    payload_json text not null,
    status varchar(32) not null,
    created_at timestamptz not null
    );

create index if not exists idx_outbox_status_created_at
    on outbox (status, created_at);

-- INBOX (consumer dedupe; Step 5/6 uses this)
create table if not exists inbox_messages (
                                              consumer_name varchar(128) not null,
    message_id varchar(128) not null,
    received_at timestamptz not null,
    primary key (consumer_name, message_id)
    );
