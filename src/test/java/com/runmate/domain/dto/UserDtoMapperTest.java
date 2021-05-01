package com.runmate.domain.dto;

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

    static final ModelMapper modelMapper = new ModelMapper();

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
}
