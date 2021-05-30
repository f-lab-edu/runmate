package com.runmate.controller;

import com.runmate.domain.crew.Crew;
import com.runmate.dto.crew.*;
import com.runmate.repository.spec.CrewOrderSpec;
import com.runmate.repository.spec.CrewUserOrderSpec;
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
        List<CrewGetDto> crews = crewService.searchCrewByRegionOrderByActivityWithPageable(request.getLocation(), pageNumber, limitCount, crewOrderSpec);
        JsonWrapper response = JsonWrapper.success(crews);

        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/{crewId}/members")
    public ResponseEntity<JsonWrapper> findAllCrewMembers(@PathVariable("crewId") long crewId,
                                                          @RequestParam @Positive int pageNumber,
                                                          @RequestParam @Positive int limitCount,
                                                          @RequestBody CrewUserSearchRequest request) {

        CrewUserOrderSpec crewUserOrderSpec = CrewUserOrderSpec.of(request.isAscending(), request.getSortBy());
        List<CrewUserGetDto> crewUsers = crewUserService.searchCrewUser(crewId, pageNumber, limitCount, crewUserOrderSpec);
        JsonWrapper response = JsonWrapper.success(crewUsers);

        return ResponseEntity.ok().body(response);
    }

}
