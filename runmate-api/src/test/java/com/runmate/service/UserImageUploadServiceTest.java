package com.runmate.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.runmate.TestActiveProfilesResolver;
import com.runmate.domain.user.Region;
import com.runmate.domain.user.User;
import com.runmate.repository.user.UserRepository;
import com.runmate.service.user.UserImageUploadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
@ActiveProfiles(inheritProfiles = false, resolver = TestActiveProfilesResolver.class)
public class UserImageUploadServiceTest {
    @Autowired
    UserImageUploadService userImageUploadService;
    @MockBean
    UserRepository userRepository;
    @MockBean
    AmazonS3Client amazonS3Client;

    final String email = "pops@naver.com";
    User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.of()
                .email(email)
                .password("pw")
                .region(Region.of().gu("gu").si("si").gun("gun").build())
                .build();
    }

    @Test
    void When_UploadTextFile_Expect_throw_IllegalException() {
        MockMultipartFile attackShellScript = new MockMultipartFile("file", "index.html", MediaType.TEXT_HTML_VALUE, "html".getBytes());

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));

        assertThrows(IllegalArgumentException.class, () -> {
            userImageUploadService.saveImage(attackShellScript, email);
        });
    }

    @Test
    void When_UploadImageFile_Expect_SaveImage() throws IOException {
        URL url = new URL("https://blog.kakaocdn.net/dn/cfcwdg/btq9E5yyy5I/17QtKpp8A7Ylg4XGUo3XN0/img.jpg");
        MockMultipartFile realImage = new MockMultipartFile("file", "sky.jpg", MediaType.IMAGE_JPEG_VALUE, url.openStream());

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));

        userImageUploadService.saveImage(realImage, email);
        verify(amazonS3Client, atLeastOnce()).putObject(any(PutObjectRequest.class));
    }
}
