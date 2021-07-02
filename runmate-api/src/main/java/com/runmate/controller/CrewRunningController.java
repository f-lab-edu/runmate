package com.runmate.controller;

import com.runmate.domain.running.Team;
import com.runmate.domain.running.TeamMember;
import com.runmate.dto.running.TeamCreationRequest;
import com.runmate.dto.running.TeamCreationResponse;
import com.runmate.dto.running.TeamMemberCreationResponse;
import com.runmate.service.crew.CrewRunningService;
import com.runmate.utils.JsonWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RequestMapping("/api/crew-running")
@RestController
public class CrewRunningController {

    private final CrewRunningService crewRunningService;

    @PostMapping("/teams")
    public ResponseEntity<JsonWrapper> createTeam(@RequestBody TeamCreationRequest request) {
        Team team = crewRunningService.createTeam(request);
        List<String> initialMemberEmails = request.getEmails();
        List<TeamMemberCreationResponse> members = initialMemberEmails.stream()
                .map(email -> crewRunningService.addMember(team, email))
                .map(teamMember -> buildTeamMemberUri(team, teamMember))
                .map(crewRunningService::convertMemberCreationResponse)
                .collect(Collectors.toList());

        JsonWrapper body = JsonWrapper.success(TeamCreationResponse.of(team, members));
        URI uri = WebMvcLinkBuilder.linkTo(CrewRunningController.class).slash("teams").slash(team.getId()).toUri();
        return ResponseEntity.created(uri).body(body);
    }

    private URI buildTeamMemberUri(Team team, TeamMember teamMember) {
        return WebMvcLinkBuilder.linkTo(CrewRunningController.class)
                .slash("teams")
                .slash(team.getId())
                .slash("members")
                .slash(teamMember.getId())
                .toUri();
    }
}
