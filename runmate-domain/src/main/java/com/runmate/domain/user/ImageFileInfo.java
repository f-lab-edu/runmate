package com.runmate.domain.user;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.LocalDateTime;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ImageFileInfo {
    @Column(name = "image_key_name")
    private String keyName;
    @Column(name = "image_created_at")
    private LocalDateTime createdAt;

    @Builder(builderMethodName = "of")
    public ImageFileInfo(String keyName, LocalDateTime createdAt) {
        this.keyName = keyName;
        this.createdAt = createdAt;
    }
}
