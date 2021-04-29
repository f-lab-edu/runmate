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
    created_at   timestamp            default CURRENT_TIMESTAMP
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

insert into user(email, introduction, name, password, si, gu, grade)
values ('you@you.com', '메일 뛰자!', 'you', 1234, 'seoul', 'nowon', 'UNRANKED');

insert into user(email, introduction, name, password, si, gu, grade)
values ('ann@ann.com', '같이 뛰자!', 'ann', 1234, 'seoul', 'gangbuk', 'BRONZE');

insert into user(email, introduction, name, password, si, gu, grade)
values ('sung@sung.com', '오래 뛰자!', 'sung', 1234, 'seoul', 'gangnam', 'SILVER');

insert into activity(user_id, distance, running_time, calories)
values (1, 10.5, '00:50:45', 500);
insert into activity(user_id, distance, running_time, calories)
values (1, 8.2, '00:38:52', 418);

insert into activity(user_id, distance, running_time, calories)
values (2, 42.195, '04:15:30', 4852);
insert into activity(user_id, distance, running_time, calories)
values (2, 42.195, '04:01:30', 5052);

insert into activity(user_id, distance, running_time, calories, created_at)
values (3, 80.195, '08:15:30', 9024, now());
insert into activity(user_id, distance, running_time, calories, created_at)
values (3, 120.195, '12:15:30', 10526, addtime(now(), '10'));
insert into activity(user_id, distance, running_time, calories, created_at)
values (3, 128, '11:47:30', 10555, addtime(now(), '100'));

