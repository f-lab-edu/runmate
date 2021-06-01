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

#users
insert into user(email, introduction, name, password, si, gu, grade)
values ('you@you.com', '메일 뛰자!', 'you', '1234', 'seoul', 'nowon', 'UNRANKED');

insert into user(email, introduction, name, password, si, gu, grade)
values ('ann@ann.com', '같이 뛰자!', 'ann', '1234', 'seoul', 'gangbuk', 'BRONZE');

insert into user(email, introduction, name, password, si, gu, grade)
values ('sung@sung.com', '오래 뛰자!', 'sung', '1234', 'seoul', 'gangnam', 'SILVER');

insert into user(email, introduction, name, password, si, gu, grade)
values ('min@gmail.com', '친구 구해요!', 'kim', '1234', 'seoul', 'nowon', 'UNRANKED');

insert into user(email, introduction, name, password, si, gu, grade)
values ('jan@naver.com', '같이 뛰실분 쪽지 주세요', 'james', '1234', 'seoul', 'gangnam', 'BRONZE');

insert into user(email, introduction, name, password, si, gu, grade)
values ('one@gmail.com', '소개', 'one', '1234', 'seoul', 'mapo', 'SILVER');

insert into user(email, introduction, name, password, si, gu, grade)
values ('two@gmail.com', '소개', 'two', '1234', 'seoul', 'dongjak', 'UNRANKED');

#activities
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

insert into activity(user_id, distance, running_time, calories, created_at)
values (5, 80.195, '08:15:30', 9024, now());
insert into activity(user_id, distance, running_time, calories, created_at)
values (5, 120.195, '12:15:30', 10526, addtime(now(), '10'));
insert into activity(user_id, distance, running_time, calories, created_at)
values (5, 128, '11:47:30', 10555, addtime(now(), '100'));

insert into activity(user_id, distance, running_time, calories, created_at)
values (6, 11.1, '00:54:10', 784, now());
insert into activity(user_id, distance, running_time, calories, created_at)
values (6, 32.1, '03:01:32', 2540, addtime(now(), '10'));
insert into activity(user_id, distance, running_time, calories, created_at)
values (6, 128, '11:47:30', 10231, addtime(now(), '100'));
insert into activity(user_id, distance, running_time, calories, created_at)
values (6, 23.13, '02:10:23', 1764, addtime(now(), '1000'));

#crews
insert into crew(description, name, si, gu, grade_limit, created_at)
values ('첫 크루입니다.', '강북크루', 'seoul', 'gangbuk', 'BRONZE', now());

insert into crew(description, name, si, gu, grade_limit, created_at)
values ('1등 크루입니다.', '강남크루', 'seoul', 'gangnam', 'SILVER', now());

insert into crew(description, name, si, gu, grade_limit, created_at)
values ('쩌리 크루입니다', '노원크루', 'seoul', 'nowon', 'UNRANKED', now());

#crew_users
insert into crew_user(user_id, crew_id, role)
values (2, 1, 'ADMIN');

insert into crew_user(user_id, crew_id, role)
values (3, 2, 'ADMIN');

insert into crew_user(user_id, crew_id, role)
values (1, 3, 'ADMIN');

insert into crew_user(user_id, crew_id, role)
values (6, 3, 'NORMAL');

insert into crew_user(user_id, crew_id, role)
values (7, 3, 'NORMAL');

#crew_join_requests
insert into crew_join_request(user_id, crew_id, created_at)
values (4, 3, now());

insert into crew_join_request(user_id, crew_id)
values (5, 1);
insert into crew_join_request(user_id, crew_id)
values (5, 2);
insert into crew_join_request(user_id, crew_id, created_at)
values (5, 3, addtime(now(), 100));