package com.runmate.domain.dto;

import com.runmate.domain.dto.user.UserGetDto;
import com.runmate.domain.dto.user.UserModificationDto;
import com.runmate.domain.user.Region;
import com.runmate.domain.user.User;
import com.runmate.repository.user.UserRepository;
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

    @DisplayName("User 에서 변경 되지 않는 필드도 그대로 남는다.")
    @Test
    public void When_Mapping_UserModificationDto_To_User_Expect_Remain_NotModifiedValue() {
        User user = userRepository.findByEmail(ADDRESS);

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
        User user = userRepository.findByEmail(ADDRESS);

        UserGetDto userGetDto = modelMapper.map(user, UserGetDto.class);

        assertEquals(user.getEmail(), userGetDto.getEmail());
        assertEquals(user.getIntroduction(), userGetDto.getIntroduction());
        assertEquals(user.getGrade(), userGetDto.getGrade());
        assertEquals(user.getRegion(), userGetDto.getRegion());
        assertEquals(user.getUsername(), userGetDto.getUsername());
        assertEquals(user.getCreatedAt(), userGetDto.getCreatedAt());
    }
}
