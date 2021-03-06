package com.runmate.controller;

import com.runmate.domain.activity.Activity;
import com.runmate.dto.activity.ActivityCreationDto;
import com.runmate.dto.activity.ActivityDto;
import com.runmate.dto.activity.ActivityStatisticsDto;
import com.runmate.service.activity.ActivityService;
import com.runmate.utils.JsonWrapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class ActivityController {
    private final ActivityService service;
    private final ModelMapper modelMapper;

    @PostMapping("/{passedEmail}/activities")
    public ResponseEntity completeActivity(@RequestParam("email") String tokenEmail,
                                           @PathVariable("passedEmail") String passedEmail,
                                           @Valid @RequestBody ActivityCreationDto activityDto) {
        if (!tokenEmail.equals(passedEmail)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("failed");
        }
        service.completeActivity(passedEmail, modelMapper.map(activityDto, Activity.class));
        return ResponseEntity.ok()
                .body("success");
    }

    @GetMapping("/{passedEmail}/activities")
    public ResponseEntity<JsonWrapper> searchLatestActivity(@PathVariable("passedEmail") String passedEmail,
                                                            @RequestParam int offset,
                                                            @RequestParam int limit) {
        List<ActivityDto> activities = service.findActivitiesWithPagination(passedEmail, offset, limit);
        JsonWrapper jsonWrapper = JsonWrapper.success(activities);
        return ResponseEntity.ok().body(jsonWrapper);
    }

    @GetMapping("/{passedEmail}/activities/statistics")
    public ResponseEntity<JsonWrapper> searchActivityBetweenDates(@PathVariable("passedEmail") String passedEmail,
                                                                  @RequestParam
                                                                  @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate from,
                                                                  @RequestParam
                                                                  @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate to) {
        ActivityStatisticsDto dto = service.findStatisticsDuringPeriod(passedEmail, from, to);
        JsonWrapper response = JsonWrapper.success(dto);

        return ResponseEntity.ok().body(response);
    }
}
