package com.dal.skillswap.service;

import com.dal.skillswap.entities.User;
import com.dal.skillswap.models.response.UserSkill;
import com.dal.skillswap.models.util.Coordinate;

import java.util.List;
import java.util.Optional;

public interface UserService {

    com.dal.skillswap.models.response.User getUserDetails(String email);

    boolean updateUserDetails(com.dal.skillswap.models.request.User user);

    void addUserSkills(String userName, List<String> skills);

    void addSkills(List<String> skills);

    Long deleteUserSkills(String userName, List<String> skills);

    Long deleteSkills(List<String> skills);

    List<com.dal.skillswap.entities.Skill> getSkills();

    List<UserSkill> getUserSkills(String userName);

    boolean setProfilePicture(String path);

    String getProfilePicture();

    User getLoggedInUser();

    String getLoggedInUserEmail();

    Coordinate getUserCoordinates(String email);

    List<com.dal.skillswap.models.response.User> getUsersWithinRange(String loggedInUserEmail, Double rangeKm, Optional<Long> skillId);
}
