package com.dal.skillswap.mapper;

import com.dal.skillswap.entities.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    com.dal.skillswap.models.response.User userEntityToDto(User user);
}
