package com.runmate.dto;

import com.runmate.TestActiveProfilesResolver;
import com.runmate.domain.activity.Activity;
import com.runmate.dto.activity.ActivityCreationDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles(inheritProfiles = false, resolver = TestActiveProfilesResolver.class)
public class ActivityDtoMapperTest {
    static final ModelMapper modelMapper = new ModelMapper();

    @DisplayName("Mapping from PostActivityDto to Activity")
    @Test
    public void When_Mapping_ActivityCreationDto_To_Activity_Expect_Same_Value() {
        ActivityCreationDto activityCreationDto = ActivityCreationDto.builder()
                .distance(12F)
                .calories(300)
                .runningTime(LocalTime.of(3, 30))
                .build();
        Activity result = modelMapper.map(activityCreationDto, Activity.class);

        assertEquals(activityCreationDto.getCalories(), result.getCalories());
        assertEquals(activityCreationDto.getDistance(), result.getDistance());
        assertEquals(activityCreationDto.getRunningTime(), result.getRunningTime());
        assertEquals(activityCreationDto.getCreatedAt(), result.getCreatedAt());
    }
}
