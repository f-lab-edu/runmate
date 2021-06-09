package com.runmate.dto.crew;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.runmate.domain.user.Region;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
public class CrewSearchRequest {
    @NotNull
    private final Region location;
    @NotNull
    private final String sortBy;
    @JsonProperty
    private final boolean isAscending;

    @Builder
    public CrewSearchRequest(Region location, String sortBy, boolean isAscending) {
        this.location = location;
        this.sortBy = sortBy;
        this.isAscending = isAscending;
    }
}
