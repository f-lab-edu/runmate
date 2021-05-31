package com.runmate.controller;

import com.runmate.domain.crew.Crew;
import com.runmate.domain.crew.CrewJoinRequest;
import com.runmate.domain.crew.CrewUser;
import com.runmate.dto.crew.*;
import com.runmate.repository.spec.CrewOrderSpec;
import com.runmate.repository.spec.CrewUserOrderSpec;
import com.runmate.service.crew.CrewJoinRequestService;
import com.runmate.service.crew.CrewService;
import com.runmate.service.crew.CrewUserService;
import com.runmate.utils.JsonWrapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Positive;
import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("api/crews")
@RestController
public class CrewController {

    private final CrewService crewService;
    private final CrewUserService crewUserService;
    private final CrewJoinRequestService crewJoinRequestService;
    private final ModelMapper modelMapper;

    @PostMapping
    public ResponseEntity<String> create(@RequestBody CrewCreationDto crewCreationDto) {
        Crew crew = modelMapper.map(crewCreationDto.getData(), Crew.class);
        Crew savedCrew = crewService.createCrew(crew, crewCreationDto.getEmail());
        URI uri = WebMvcLinkBuilder.linkTo(CrewController.class).slash(savedCrew.getId()).toUri();
        return ResponseEntity.created(uri).body("success");
    }

    @DeleteMapping("/{crewId}")
    public ResponseEntity<?> delete(@PathVariable("crewId") long crewId,
                                    @RequestBody String requestEmail) {

        crewService.deleteCrew(crewId, requestEmail);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<JsonWrapper> findCrewsWithLocation(@RequestParam @Positive int pageNumber,
                                                             @RequestParam @Positive int limitCount,
                                                             @RequestBody CrewSearchRequest request) {

        CrewOrderSpec crewOrderSpec = CrewOrderSpec.of(request.isAscending(), request.getSortBy());
        List<CrewGetDto> crews = crewService.searchCrewByRegionOrderByActivityWithPageable(request.getLocation(), calculateOffset(pageNumber, limitCount), limitCount, crewOrderSpec);
        JsonWrapper response = JsonWrapper.success(crews);

        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/{crewId}/members")
    public ResponseEntity<JsonWrapper> findAllCrewMembers(@PathVariable("crewId") long crewId,
                                                          @RequestParam @Positive int pageNumber,
                                                          @RequestParam @Positive int limitCount,
                                                          @RequestBody CrewUserSearchRequest request) {

        CrewUserOrderSpec crewUserOrderSpec = CrewUserOrderSpec.of(request.isAscending(), request.getSortBy());
        List<CrewUserGetDto> crewUsers = crewUserService.searchCrewUser(crewId, calculateOffset(pageNumber, limitCount), limitCount, crewUserOrderSpec);
        JsonWrapper response = JsonWrapper.success(crewUsers);

        return ResponseEntity.ok().body(response);
    }

    private int calculateOffset(int pageNumber, int limitCount) {
        return (pageNumber - 1) * limitCount;
    }


    @GetMapping("/{crewId}/requests")
    public ResponseEntity<JsonWrapper> findAllJoinRequests(@PathVariable("crewId") long crewId,
                                                           @RequestParam @Positive int pageNumber,
                                                           @RequestParam @Positive int limitCount) {

        List<CrewJoinRequestGetDto> joinRequests = crewJoinRequestService.searchJoinRequestByCrewWithPageable(crewId, pageNumber, limitCount);
        JsonWrapper response = JsonWrapper.success(joinRequests);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/{crewId}/requests")
    public ResponseEntity<String> sendJoinRequest(@PathVariable("crewId") long crewId,
                                                  @RequestBody String email) {

        CrewJoinRequest joinRequest = crewJoinRequestService.sendJoinRequest(crewId, email);
        URI uri = WebMvcLinkBuilder.linkTo(CrewController.class).slash(crewId).slash("requests").slash(joinRequest.getId()).toUri();
        return ResponseEntity.created(uri).body("success");
    }

    @PostMapping("/{crewId}/members")
    public ResponseEntity<String> approveJoinRequest(@PathVariable("crewId") long crewId,
                                                     @RequestBody long requestId) {

        CrewUser crewUser = crewJoinRequestService.approveJoinRequest(crewId, requestId);
        URI uri = WebMvcLinkBuilder.linkTo(CrewController.class).slash(crewId).slash("members").slash(crewUser.getId()).toUri();
        return ResponseEntity.created(uri).body("success");
    }

    @DeleteMapping("/{crewId}/members/{crewUserId}")
    public ResponseEntity<?> deleteCrewMember(@PathVariable("crewId") long crewId,
                                              @PathVariable("crewUserId") long crewUserId,
                                              @RequestBody String requestUserEmail) {

        crewUserService.delete(crewId, crewUserId, requestUserEmail);
        return ResponseEntity.noContent().build();
    }
}
