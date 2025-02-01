package com.dal.skillswap.mapper;

import com.dal.skillswap.entities.User;
import com.dal.skillswap.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {UserMapperImpl.class})
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void testUserEntityToDto() {
        User userEntity = new User();
        userEntity.setId(1L);
        userEntity.setFirstName("test");
        userEntity.setLastName("test");
        userEntity.setEmail("test@test.com");
        userEntity.setRole(UserRole.USER);

        com.dal.skillswap.models.response.User userModel = userMapper.userEntityToDto(userEntity);

        assertEquals(userEntity.getId(), userModel.getId());
    }
}
