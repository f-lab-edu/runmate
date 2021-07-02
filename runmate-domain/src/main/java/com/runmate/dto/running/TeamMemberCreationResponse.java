package com.runmate.dto.running;

import com.runmate.domain.user.Grade;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.net.URI;

@Getter
@RequiredArgsConstructor
public class TeamMemberCreationResponse {
    private final long id;
    private final String name;
    private final String email;
    private final Grade grade;
    @Setter private URI uri;
}
