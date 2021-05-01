package com.runmate.util;

import com.runmate.domain.activity.Activity;
import com.runmate.domain.dto.activity.PostActivityDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
public class ModelMapperTest {
    static final ModelMapper modelMapper = new ModelMapper();

    @DisplayName("Mapping from PostActivityDto to Activity")
    @Test
    public void When_Mapping_PostActivityDto_To_Activity_Expect_Same_Value() {
        PostActivityDto postActivityDto = PostActivityDto.builder()
                .distance(12F)
                .calories(300)
                .runningTime(LocalTime.of(3, 30))
                .build();
        Activity result = modelMapper.map(postActivityDto, Activity.class);

        assertEquals(postActivityDto.getCalories(), result.getCalories());
        assertEquals(postActivityDto.getDistance(), result.getDistance());
        assertEquals(postActivityDto.getRunningTime(), result.getRunningTime());
        assertEquals(postActivityDto.getCreatedAt(), result.getCreatedAt());
    }
}
