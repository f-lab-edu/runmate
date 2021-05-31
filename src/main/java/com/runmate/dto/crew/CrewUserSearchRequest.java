package com.runmate.dto.crew;

import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
public class CrewUserSearchRequest {
    @NotNull
    private final String sortBy;

    private final boolean isAscending;

    public CrewUserSearchRequest(@NotNull String sortBy, boolean isAscending) {
        this.sortBy = sortBy;
        this.isAscending = isAscending;
    }
}
