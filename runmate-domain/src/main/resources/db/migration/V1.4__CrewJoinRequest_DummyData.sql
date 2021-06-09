insert into crew_join_request(user_id, crew_id, created_at)
values (4, 3, now());

insert into crew_join_request(user_id, crew_id)
values (5, 1);
insert into crew_join_request(user_id, crew_id)
values (5, 2);
insert into crew_join_request(user_id, crew_id, created_at)
values (5, 3, addtime(now(), 100));
