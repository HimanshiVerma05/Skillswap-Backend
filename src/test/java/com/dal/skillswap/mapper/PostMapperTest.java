package com.dal.skillswap.mapper;

import com.dal.skillswap.entities.Post;
import com.dal.skillswap.entities.Skill;
import com.dal.skillswap.entities.User;
import com.dal.skillswap.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {PostMapperImpl.class})
class PostMapperTest {

    @Autowired
    private PostMapper postMapper;

    @Test
    public void testToPostModel() {

        Post postEntity = new Post();
        postEntity.setPostId(1L);
        User userEntity = new User();
        Skill skillEntity = new Skill();

        postEntity.setUser(userEntity);
        postEntity.setSkill(skillEntity);

        com.dal.skillswap.models.response.Post postModel = postMapper.toPostModel(postEntity);
        assertEquals(postEntity.getPostId(), postModel.getPostId());
    }

    @Test
    public void testToPostEntity() {
        com.dal.skillswap.models.response.Post postModel = new com.dal.skillswap.models.response.Post();
        postModel.setPostId(1L);

        postModel.setUser(getSampleUserResponse());
        postModel.setSkill(getSkillResponse());

        Post postEntity = postMapper.toPostEntity(postModel);
        assertEquals(postModel.getPostId(), postEntity.getPostId());
    }

    @Test
    public void testToUserModel() {
        User userEntity = new User();
        userEntity.setId(1L);
        userEntity.setFirstName("test");
        userEntity.setLastName("test");

        com.dal.skillswap.models.response.User userModel = postMapper.toUserModel(userEntity);
        assertEquals(userEntity.getId(), userModel.getId());
    }

    private com.dal.skillswap.models.response.User getSampleUserResponse() {
        return new com.dal.skillswap.models.response.User(1L, "test", "test",
                "test@test.com", "", UserRole.USER, "B3H1B9", true, null,
                20, null, null, null, null, true,
                null, null, null, null);
    }

    private com.dal.skillswap.models.response.Skill getSkillResponse() {
        return new com.dal.skillswap.models.response.Skill(1L, "testSkill");
    }

}
