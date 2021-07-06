insert into team(id, title, goal_total_distance, goal_running_seconds, result_total_distance,
                 result_total_running_seconds)
values (1, "let's run1", 10, 3600, 0, 0);

insert into team_member(crew_user_id, team_id, individual_distance, individual_running_seconds)
values (1, 1, 0, 0);

insert into team_member(crew_user_id, team_id, individual_distance, individual_running_seconds)
values (2, 1, 0, 0);

insert into team_member(crew_user_id, team_id, individual_distance, individual_running_seconds)
values (3, 1, 0, 0);

insert into team_member(crew_user_id, team_id, individual_distance, individual_running_seconds)
values (4, 1, 0, 0);

insert into team_member(crew_user_id, team_id, individual_distance, individual_running_seconds)
values (5, 1, 0, 0);

update team set leader_id=1 where id = 1;