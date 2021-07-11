package com.runmate.service;

import com.runmate.TestActiveProfilesResolver;
import com.runmate.redis.GoalForTempStore;
import com.runmate.redis.TeamInfo;
import com.runmate.exception.CurrentIsNotRunningTimeException;
import com.runmate.exception.MemberNotIncludedTeamException;
import com.runmate.redis.MemberInfoRepository;
import com.runmate.redis.TeamInfoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.time.LocalDateTime.now;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles(resolver = TestActiveProfilesResolver.class, inheritProfiles = false)
public class RunningDataMoveUnitTest {
    @Autowired
    RunningDataMoveService runningDataMoveService;
    @MockBean
    TeamInfoRepository teamInfoRepository;
    @MockBean
    MemberInfoRepository memberInfoRepository;

    final Long teamId = 1L;
    final Long memberId = 1L;

    @Test
    void When_ParticipateInRunning_OverTheEndTime_Expect_Throws_CurrentIsNotRunningTimeException() {
        //given
        final LocalDateTime startTime = now().minus(2, ChronoUnit.HOURS);
        final Long runningTime = 3600L;

        List<Long> totalMembers = new ArrayList<>();
        totalMembers.add(memberId);

        GoalForTempStore goal = GoalForTempStore.builder()
                .runningSeconds(runningTime)
                .startedAt(startTime)
                .distance(10F)
                .build();

        TeamInfo teamInfo = TeamInfo.builder()
                .teamId(teamId)
                .adminId(memberId)
                .totalMembers(totalMembers)
                .goal(goal)
                .build();

        when(teamInfoRepository.findById(teamId)).thenReturn(Optional.of(teamInfo));

        //then
        assertThrows(CurrentIsNotRunningTimeException.class, () -> {
            runningDataMoveService.persistRunningDataToMem(teamId, memberId);
        });
    }

    @Test
    void When_ParticipateInRunning_NotStartedRunning_Expect_Throws_CurrentIsNotRunningTimeException() {
        final LocalDateTime startTime = now().plus(3, ChronoUnit.HOURS);
        final Long runningTime = 3600L;

        List<Long> totalMembers = new ArrayList<>();
        totalMembers.add(memberId);

        GoalForTempStore goal = GoalForTempStore.builder()
                .runningSeconds(runningTime)
                .startedAt(startTime)
                .distance(10F)
                .build();

        TeamInfo teamInfo = TeamInfo.builder()
                .teamId(teamId)
                .adminId(memberId)
                .totalMembers(totalMembers)
                .goal(goal)
                .build();

        when(teamInfoRepository.findById(teamId)).thenReturn(Optional.of(teamInfo));

        //then
        assertThrows(CurrentIsNotRunningTimeException.class, () -> {
            runningDataMoveService.persistRunningDataToMem(teamId, memberId);
        });
    }

    @Test
    void When_ParticipateInRunning_NotIncludedInTeam_Expect_Throws_MemberNotIncludedTeamException() {
        //given
        final LocalDateTime startTime = now().minus(10, ChronoUnit.MINUTES);
        final Long runningTime = 3600L;

        List<Long> totalMembers = new ArrayList<>();//none

        GoalForTempStore goal = GoalForTempStore.builder()
                .runningSeconds(runningTime)
                .startedAt(startTime)
                .distance(10F)
                .build();

        TeamInfo teamInfo = TeamInfo.builder()
                .teamId(teamId)
                .adminId(memberId)
                .totalMembers(totalMembers)
                .goal(goal)
                .build();

        when(teamInfoRepository.findById(teamId)).thenReturn(Optional.of(teamInfo));

        //then
        assertThrows(MemberNotIncludedTeamException.class, () -> {
            runningDataMoveService.persistRunningDataToMem(teamId, memberId);
        });
    }

    @Test
    void When_ParticipateInRunning_BetweenRunningTime_And_IncludedMember_Expect_OnlineMemberAdded() {
        //given
        final LocalDateTime startTime = now().minus(10, ChronoUnit.MINUTES);
        final Long runningTime = 3600L;

        List<Long> totalMembers = new ArrayList<>();
        totalMembers.add(memberId);

        GoalForTempStore goal = GoalForTempStore.builder()
                .runningSeconds(runningTime)
                .startedAt(startTime)
                .distance(10F)
                .build();

        TeamInfo teamInfo = TeamInfo.builder()
                .teamId(teamId)
                .adminId(memberId)
                .totalMembers(totalMembers)
                .goal(goal)
                .build();
        when(teamInfoRepository.findById(teamId)).thenReturn(Optional.of(teamInfo));

        //when
        runningDataMoveService.persistRunningDataToMem(teamId, memberId);

        //then
        verify(teamInfoRepository, atLeastOnce()).save(any(TeamInfo.class));
    }
}
