package com.dal.skillswap.mapper;

import com.dal.skillswap.entities.Post;
import com.dal.skillswap.entities.Skill;
import com.dal.skillswap.models.response.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PostMapper {

    com.dal.skillswap.models.response.Post toPostModel(Post post);

    Post toPostEntity(com.dal.skillswap.models.response.Post post);

    User toUserModel(com.dal.skillswap.entities.User user);

    com.dal.skillswap.entities.User toUserEntity(User user);

    Skill toSkillEntity(com.dal.skillswap.models.response.Skill skill);

    com.dal.skillswap.models.response.Skill toSkillModel(Skill skill);
}
