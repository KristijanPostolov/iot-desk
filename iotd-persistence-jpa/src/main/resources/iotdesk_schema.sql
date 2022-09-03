create table device
(
    id         bigserial primary key,
    name       varchar(100) not null,
    state      smallint     not null,
    created_at timestamp
);

create table device_channel_id
(
    device_id  bigserial not null,
    channel_id UUID      not null,
    primary key (device_id, channel_id),
    constraint fk__device_channel_id__device foreign key (device_id) references device (id)
);

create table device_parameter
(
    id        bigserial primary key,
    device_id bigserial not null,
    anchor    integer   not null,
    name      varchar(50),
    constraint fk__device_parameter__device foreign key (device_id) references device (id)
);

create table parameter_snapshot
(
    id              bigserial primary key,
    parameter_id    bigserial not null,
    value_timestamp timestamp,
    value           double precision
);



