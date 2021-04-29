package com.runmate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.runmate.configure.jwt.JwtAuthenticationFilter;
import com.runmate.configure.jwt.JwtProvider;
import com.runmate.domain.activity.Activity;
import com.runmate.domain.activity.RunningTime;
import com.runmate.domain.dto.ActivityDto;
import com.runmate.domain.dto.ActivityStatisticsDto;
import com.runmate.domain.dto.AuthRequest;
import com.runmate.domain.user.User;
import com.runmate.repository.activity.ActivityRepository;
import com.runmate.repository.user.UserRepository;
import com.runmate.service.activity.ActivityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static com.runmate.controller.exception.GlobalExceptionHandler.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ActivityControllerTest {
    MockMvc mockMvc;

    @Autowired
    ActivityRepository activityRepository;

    @Autowired
    WebApplicationContext ctx;
    @Autowired
    JwtProvider provider;
    @MockBean
    ActivityService activityService;

    User user;
    static final String ADDRESS = "you@you.com";
    @Autowired
    UserRepository userRepository;

    ObjectMapper mapper = new ObjectMapper();

    String token;

    @BeforeEach
    public void login() throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(ctx)
                .addFilter(new JwtAuthenticationFilter(provider))
                .addFilter(new CharacterEncodingFilter("utf8"))
                .build();
        user = userRepository.findByEmail(ADDRESS);

        AuthRequest authRequest = new AuthRequest();
        authRequest.setEmail(user.getEmail());
        authRequest.setPassword(user.getPassword());

        String jsonBody = mapper.writeValueAsString(authRequest);

        MvcResult result = mockMvc.perform(post("/api/auth/local/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
                .andExpect(status().isOk())
                .andExpect(header().exists("Authorization"))
                .andReturn();
        token = result.getResponse().getHeader("Authorization").replace("Bearer ", "");
    }

    @Test
    public void When_Complete_Activity_Expect_Status_OK_Body_success() throws Exception {
        String jsonBody = "{\n" +
                "  \"distance\": 12.0,\n" +
                "  \"runningTime\": \"02:30:30\",\n" +
                "  \"calories\": 1200\n" +
                "}";

        mockMvc.perform(post("/api/users/" + user.getEmail() + "/activities")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody)
                .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("success"));

        verify(activityService, times(1)).completeActivity(anyString(), ArgumentMatchers.any(Activity.class));
    }

    @Test
    public void When_InvalidCompleteActivity_Expect_Status_ClientError() throws Exception {
        //running Time null,minus distance
        String invalidJsonBody = "{\n" +
                "  \"distance\": -12.0,\n" +
                "  \"calories\": 1200\n" +
                "}";

        mockMvc.perform(post("/api/users/" + user.getEmail() + "/activities")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJsonBody)
                .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(INVALID_REQUEST_BODY_MESSAGE));
    }

    @Test
    public void When_Search_ActivityStatistics_Expect_Status_OK_Body_JsonWrapper() throws Exception {
        //given
        when(activityService.findStatisticsDuringPeriod(anyString(), ArgumentMatchers.any(LocalDate.class), ArgumentMatchers.any(LocalDate.class)))
                .thenReturn(ActivityStatisticsDto.of(0, 0, RunningTime.of(2, 30, 0), LocalTime.of(2, 30, 0), 0));

        LocalDate from = LocalDate.of(2020, 12, 04);
        LocalDate to = LocalDate.of(2021, 03, 20);


        System.out.println(from.toString());
        System.out.println(to.toString());

        //when
        mockMvc.perform(get("/api/users/" + user.getEmail() + "/activities/statistics?from=" + from + "&to=" + to)
                .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", is(notNullValue())))
                .andExpect(jsonPath("$.error", is(nullValue())));
    }

    @Test
    public void When_Search_LatestActivity_Expect_Status_OK_Body_JsonWrapper() throws Exception {
        //given
        when(activityService.findActivitiesWithPagination(anyString(), anyInt(), anyInt())).thenReturn(new ArrayList<ActivityDto>());

        //then
        mockMvc.perform(get("/api/users/" + user.getEmail() + "/activities?offset=3&limit=10")
                .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", is(notNullValue())))
                .andExpect(jsonPath("$.error", is(nullValue())));
    }

    @Test
    public void When_Search_UnAuthorizedUser_Expect_Status_UnAuthorized() throws Exception {
        mockMvc.perform(get("/api/users/" + user.getEmail() + "/activities?offset=5&limit=5"))
                .andExpect(status().isUnauthorized());
    }
}
