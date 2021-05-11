package com.runmate.domain.dto.activity;

import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ActivityCreationDto {
    @Positive(message = "Field must be greater than 0.")
    private float distance;

    @NotNull(message = "Field can't be null value")
    private LocalTime runningTime;

    @Positive(message = "Field must be greater than 0.")
    private int calories;

    private LocalDateTime createdAt=LocalDateTime.now();

    @Builder
    public ActivityCreationDto(float distance, LocalTime runningTime, int calories) {
        this.distance=distance;
        this.runningTime=runningTime;
        this.calories=calories;
    }
}
