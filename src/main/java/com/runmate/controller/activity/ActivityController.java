package com.runmate.controller.activity;

import com.runmate.domain.activity.Activity;
import com.runmate.utils.JsonWrapper;
import com.runmate.domain.dto.ActivityDto;
import com.runmate.domain.dto.ActivityStatisticsDto;
import com.runmate.service.activity.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class ActivityController {
    private final ActivityService service;

    @PostMapping("/{passedEmail}/activities")
    public ResponseEntity completeActivity(@RequestParam("email") String tokenEmail,
                                           @PathVariable("passedEmail") String passedEmail,
                                           @RequestBody Activity activity) {
        if (!tokenEmail.equals(passedEmail)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("failed");
        }
        service.completeActivity(passedEmail, activity);
        return ResponseEntity.ok()
                .body("success");
    }

    @GetMapping("/{passedEmail}/activities")
    public ResponseEntity<JsonWrapper> searchLatestActivity(@PathVariable("passedEmail") String passedEmail,
                                                            @RequestParam int offset,
                                                            @RequestParam int limit) {
        List<ActivityDto> activities = service.findActivitiesWithPagination(passedEmail, offset, limit);
        JsonWrapper jsonWrapper = JsonWrapper.builder()
                .data(activities)
                .error(null)
                .build();
        return ResponseEntity.ok().body(jsonWrapper);
    }

    @GetMapping("/{passedEmail}/activities/statistics")
    public ResponseEntity<JsonWrapper> searchActivityBetweenDates(@PathVariable("passedEmail") String passedEmail,
                                                                  @RequestParam
                                                                  @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate from,
                                                                  @RequestParam
                                                                  @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate to) {
        ActivityStatisticsDto dto = service.findStatisticsDuringPeriod(passedEmail, from, to);
        JsonWrapper response = JsonWrapper.builder()
                .data(dto)
                .error(null)
                .build();

        return ResponseEntity.ok().body(response);
    }
}
