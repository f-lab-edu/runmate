package com.runmate.domain.running;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalTime;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GoalTest {

    @ParameterizedTest
    @MethodSource("provideArgumentsForNormalCase")
    @DisplayName("정상적인 입력으로 목표 시간과 목표 거리가 주어졌을때 1km 당 초의 포맷으로 반환됨")
    void GivenNormalInput_WhenCalculatePace_ThenReturnSecondsPerKilometerFormat(float distance, long runningSeconds, LocalTime expected) {
        //given
        Goal goal = Goal.builder().totalDistance(distance).totalRunningSeconds(runningSeconds).build();

        //when
        LocalTime pace = goal.calculatePace();

        //then
        assertThat(pace).isEqualTo(expected);
    }

    private Stream<Arguments> provideArgumentsForNormalCase() {
        return Stream.of(
                Arguments.of(1.0f, 60 * 5, LocalTime.of(0, 5, 0)),
                Arguments.of(3.0f, 60 * 12 + 30, LocalTime.of(0, 4, 10)),
                Arguments.of(11.04f, 60 * 54 + 31, LocalTime.of(0, 4, 56)),
                Arguments.of(5.03f, 60 * 27 + 14, LocalTime.of(0, 5, 25)),
                Arguments.of(6.77f, 60 * 39 + 9, LocalTime.of(0, 5, 47)),
                Arguments.of(42.05f, 60 * 60 * 3 + 60 * 32 + 23, LocalTime.of(0, 5, 3)),
                Arguments.of(1.00f, 60 * 60 * 23 + 60 * 59 + 59, LocalTime.of(23, 59, 59))
        );
    }

    @Test
    @DisplayName("속력이 24시간을 넘어가면 23:59:59를 반환")
    void GivenPaceIsOverMaxSeconds_WhenCalculatePace_ThenReturn235959() {
        //given
        Goal goal = Goal.builder().totalDistance(0.01f).totalRunningSeconds(60 * 60 * 2 + 60 * 32 + 4).build();

        //when
        LocalTime pace = goal.calculatePace();

        //then
        assertThat(pace).isEqualTo(LocalTime.of(23, 59, 59));
    }
}