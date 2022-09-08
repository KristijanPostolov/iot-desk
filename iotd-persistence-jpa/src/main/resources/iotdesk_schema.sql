create table device
(
    id         bigserial primary key,
    name       varchar(100) not null,
    state      smallint     not null,
    created_at timestamp
);

create table device_channel_id
(
    id         bigserial primary key,
    device_id  bigserial   not null,
    channel_id varchar(36) not null,
    constraint fk__device_channel_id__device foreign key (device_id) references device (id),
    constraint unique__device_id unique (device_id)
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

create table device_command
(
    id              bigserial primary key,
    command_id      varchar(36)  not null,
    content         varchar(255) not null,
    device_id       bigserial    not null,
    sent_at         timestamp    not null,
    acknowledged_at timestamp,
    ack_status      smallint     not null,
    constraint fk__device_command__device foreign key (device_id) references device (id),
    constraint unique__command_id unique (command_id)
);

create table user_Account
(
    id       bigserial primary key,
    username varchar(50),
    password varchar(255)
);

