package com.dal.skillswap.mapper;

import com.dal.skillswap.models.response.Skill;
import com.dal.skillswap.models.response.User;
import com.dal.skillswap.models.response.UserSkill;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SkillMapper {
    User toUserModel(com.dal.skillswap.entities.User user);

    UserSkill toUserSkillModel(com.dal.skillswap.entities.User userSkill);

    Skill toSkillModel(com.dal.skillswap.entities.Skill skill);

    com.dal.skillswap.entities.Skill toSkillEntity(Skill skill);

}
