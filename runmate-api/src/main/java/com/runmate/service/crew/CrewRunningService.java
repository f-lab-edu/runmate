package com.runmate.service.crew;

import com.runmate.domain.crew.CrewUser;
import com.runmate.domain.running.Team;
import com.runmate.domain.running.TeamMember;
import com.runmate.domain.user.User;
import com.runmate.dto.running.TeamCreationRequest;
import com.runmate.dto.running.TeamMemberCreationResponse;
import com.runmate.exception.NotFoundCrewUserException;
import com.runmate.exception.NotFoundTeamException;
import com.runmate.exception.NotFoundTeamMemberException;
import com.runmate.exception.NotFoundUserEmailException;
import com.runmate.infra.FirebaseCloudMessageService;
import com.runmate.repository.crew.CrewUserRepository;
import com.runmate.repository.running.TeamMemberQueryRepository;
import com.runmate.repository.running.TeamMemberRepository;
import com.runmate.repository.running.TeamRepository;
import com.runmate.repository.user.UserDeviceQueryRepository;
import com.runmate.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;

@Transactional
@RequiredArgsConstructor
@Service
public class CrewRunningService {
    private static final String URI_SEPARATOR = "/";

    private final UserRepository userRepository;
    private final CrewUserRepository crewUserRepository;
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamMemberQueryRepository teamMemberQueryRepository;
    private final UserDeviceQueryRepository userDeviceQueryRepository;

    private final FirebaseCloudMessageService firebaseCloudMessageService;

    public Team createTeam(TeamCreationRequest request) {
        CrewUser leaderCrewUser = crewUserRepository.findById(request.getLeaderId()).orElseThrow(NotFoundCrewUserException::new);
        Team createdTeam = teamRepository.save(Team.from(request));
        TeamMember leader = TeamMember.builder().team(createdTeam).crewUser(leaderCrewUser).build();
        createdTeam.assignLeader(leader);
        return createdTeam;
    }

    public TeamMember addMember(Team team, String email) {
        User user = userRepository.findByEmail(email).orElseThrow(NotFoundUserEmailException::new);
        CrewUser crewUser = crewUserRepository.findByUser(user).orElseThrow(NotFoundCrewUserException::new);
        team.validateMember(crewUser);
        TeamMember teamMember = TeamMember.builder().team(team).crewUser(crewUser).build();

        userDeviceQueryRepository.findAllDeviceTokenByEmail(email)
                .forEach(deviceToken -> firebaseCloudMessageService.sendMessageTo(deviceToken, "title", "body"));
        return teamMemberRepository.save(teamMember);
    }

    @Transactional(readOnly = true)
    public Team findTeamById(Long teamId) {
        return teamRepository.findById(teamId).orElseThrow(NotFoundTeamException::new);
    }

    @Transactional(readOnly = true)
    public TeamMemberCreationResponse convertMemberCreationResponse(URI uri) {
        String[] pathParts = uri.getPath().split(URI_SEPARATOR);
        long teamMemberId = Long.parseLong(pathParts[pathParts.length - 1]);
        TeamMemberCreationResponse response = teamMemberQueryRepository.findByIdWithUser(teamMemberId).orElseThrow(NotFoundTeamMemberException::new);
        response.setUri(uri);
        return response;
    }
}
