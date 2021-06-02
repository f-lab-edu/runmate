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