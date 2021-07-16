drop table if exists user cascade;
drop table if exists activity cascade;
drop table if exists crew cascade;
drop table if exists crew_user cascade;
drop table if exists crew_join_request cascade;

create table user
(
    id           bigint primary key auto_increment,
    email        varchar(30) not null unique,
    introduction varchar(255),
    name         varchar(20),
    password     varchar(255),
    si           varchar(20),
    gu           varchar(20),
    gun          varchar(20),
    grade        varchar(20) not null default 'UNRANKED',
    created_at   timestamp            default CURRENT_TIMESTAMP,
    image_key_name varchar(255),
    image_created_at timestamp null
) default character set utf8;

create table activity
(
    id           bigint primary key auto_increment,
    user_id      bigint       not null,
    distance     float(10, 3) not null,
    running_time time         not null,
    calories     int,
    created_at   timestamp default CURRENT_TIMESTAMP
) default character set utf8;
alter table activity
    ADD FOREIGN KEY (user_id) REFERENCES user (id);

create table crew
(
    id          bigint primary key auto_increment,
    description varchar(255),
    name        varchar(20) not null,
    si          varchar(20) not null,
    gu          varchar(20),
    gun         varchar(20),
    grade_limit varchar(20) not null default 'UNRANKED',
    created_at  timestamp            default CURRENT_TIMESTAMP
) default character set utf8;

create table crew_user
(
    id          bigint primary key auto_increment,
    user_id     bigint NOT NULL,
    crew_id     bigint NOT NULL,
    role        varchar(20) NOT NULL,
    created_at  timestamp default CURRENT_TIMESTAMP,

    foreign key (user_id) references user(id) on delete cascade,
    foreign key (crew_id) references crew(id) on delete cascade
) default character set utf8;

create table crew_join_request
(
    id          bigint primary key auto_increment,
    user_id     bigint NOT NULL ,
    crew_id     bigint NOT NULL ,
    created_at  timestamp default CURRENT_TIMESTAMP,

    foreign key (user_id) references user(id) on delete cascade ,
    foreign key (crew_id) references crew(id) on delete cascade
) default character set utf8;

create table user_device
(
    id          bigint primary key auto_increment,
    user_id     bigint not null ,
    device_alias varchar(255),
    device_token varchar(255) not null,

    foreign key (user_id) references user(id) on delete cascade
) default character set utf8;