package com.runmate.domain.dto;

import com.runmate.domain.dto.user.UserCreationDto;
import com.runmate.domain.dto.user.UserGetDto;
import com.runmate.domain.dto.user.UserModificationDto;
import com.runmate.domain.user.Region;
import com.runmate.domain.user.User;
import com.runmate.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class UserDtoMapperTest {
    @Autowired
    UserRepository userRepository;
    @Autowired
    ModelMapper modelMapper;

    static final String ADDRESS = "you@you.com";
    User user;

    @BeforeEach
    public void setUp() {
        user = userRepository.findByEmail(ADDRESS);
    }

    @DisplayName("User 에서 변경 되지 않는 필드도 그대로 남는다.")
    @Test
    public void When_Mapping_UserModificationDto_To_User_Expect_Remain_NotModifiedValue() {
        UserModificationDto userModificationDto = UserModificationDto.builder()
                .username("youAndI")
                .region(new Region("seoul", "gangbuk", null))
                .introduction("let'go")
                .id(1L)
                .build();

        modelMapper.map(userModificationDto, user);

        assertNotNull(user.getEmail());

        assertEquals(user.getUsername(), userModificationDto.getUsername());
        assertEquals(user.getIntroduction(), userModificationDto.getIntroduction());
        assertEquals(user.getRegion().getGu(), userModificationDto.getRegion().getGu());
    }

    @DisplayName("User객체를 UserGetDto로 변경")
    @Test
    public void When_Mapping_User_To_UserGetDto_Expect_Same_Value() {
        UserGetDto userGetDto = modelMapper.map(user, UserGetDto.class);

        assertEquals(user.getEmail(), userGetDto.getEmail());
        assertEquals(user.getIntroduction(), userGetDto.getIntroduction());
        assertEquals(user.getGrade(), userGetDto.getGrade());
        assertEquals(user.getRegion(), userGetDto.getRegion());
        assertEquals(user.getUsername(), userGetDto.getUsername());
        assertEquals(user.getCreatedAt(), userGetDto.getCreatedAt());
    }

    @DisplayName("UserCreationDto객체를 User로 변경")
    @Test
    public void When_Mapping_UserCreationDto_To_User_Expect_Same_Value() {
        UserCreationDto userCreationDto = UserCreationDto.builder().username("messi")
                .email("ppap@ppap.com")
                .password("321")
                .username("ppap")
                .region(new Region("is", "ug", "nug"))
                .introduction("intro~")
                .build();
        modelMapper.map(userCreationDto, user);

        assertEquals(user.getEmail(), userCreationDto.getEmail());
        assertEquals(user.getIntroduction(), userCreationDto.getIntroduction());
        assertEquals(user.getPassword(), userCreationDto.getPassword());
        assertEquals(user.getRegion(), userCreationDto.getRegion());
        assertEquals(user.getUsername(), userCreationDto.getUsername());
        assertEquals(user.getCreatedAt(), userCreationDto.getCreatedAt());
    }
}
