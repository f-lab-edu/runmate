package com.runmate.controller;

import com.runmate.domain.running.Team;
import com.runmate.domain.running.TeamMember;
import com.runmate.dto.running.TeamCreationRequest;
import com.runmate.service.crew.CrewRunningService;
import com.runmate.utils.JsonWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        List<URI> memberUris = initialMemberEmails.stream()
                .map(email -> crewRunningService.addMember(team, email))
                .map(teamMember -> buildTeamMemberUri(team, teamMember))
                .collect(Collectors.toList());

        JsonWrapper body = JsonWrapper.success(memberUris);
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
