
create table ticker_db(
    id bigint auto_increment PRIMARY KEY,
    base_unit varchar(5) not null,
    last decimal(19,6) not null,
    open decimal(19,6) not null,
    sell decimal(19,6) not null,
    buy decimal(19,6) not null,
    at bigint not null
);

create table ema_db(
    id bigint auto_increment PRIMARY KEY,
    baseUnit varchar(5) not null,
    ema_50 decimal(19,6) not null,
    ema_200 decimal(19,6) not null,
    at bigint not null
);