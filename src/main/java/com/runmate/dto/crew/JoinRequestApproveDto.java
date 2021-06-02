package com.runmate.dto.crew;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class JoinRequestApproveDto {
    private final String email;
    private final long requestId;
}
