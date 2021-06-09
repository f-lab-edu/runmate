package com.runmate.domain.running;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Position {
    private final float latitude;
    private final float longitude;
}
