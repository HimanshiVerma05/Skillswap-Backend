package com.dal.skillswap.service.impl;

import com.dal.skillswap.entities.Skill;
import com.dal.skillswap.entities.User;
import com.dal.skillswap.entities.UserSkill;
import com.dal.skillswap.enums.UserRole;
import com.dal.skillswap.mapper.UserMapper;
import com.dal.skillswap.mapper.UserSkillMapper;
import com.dal.skillswap.models.util.Coordinate;
import com.dal.skillswap.repository.SkillRepository;
import com.dal.skillswap.repository.UserRepository;
import com.dal.skillswap.repository.UserSkillRepository;
import com.dal.skillswap.service.LocationService;
import com.dal.skillswap.service.UserService;
import com.mysql.cj.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private static final int MULTIPLICATION_KM_TO_M = 1000;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserSkillMapper userSkillMapper;

    @Autowired
    private LocationService locationService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserSkillRepository userSkillRepository;
    @Autowired
    private SkillRepository skillRepository;

    @Override
    public com.dal.skillswap.models.response.User getUserDetails(String email) throws UsernameNotFoundException {
        User user = getUser(email);
        return userMapper.userEntityToDto(user);
    }

    @Override
    public boolean updateUserDetails(com.dal.skillswap.models.request.User requestUser) {
        User user = getLoggedInUser();
        User mappedUser = mapUserRequestToEntity(requestUser, user);
        userRepository.save(mappedUser);
        return true;
    }

    @Override
    public void addUserSkills(String userName, List<String> skillNames) {
        User loggedInUser = getUser(userName);
        List<UserSkill> userSkills = skillNames.stream()
                .map(skillName -> skillRepository.findBySkillName(skillName)
                        .orElseGet(() -> skillRepository.save(new Skill(skillName))))
                .map(skill -> new UserSkill(loggedInUser, skill))
                .collect(Collectors.toList());

        userSkillRepository.saveAll(userSkills);
    }

    @Override
    public void addSkills(List<String> skillNames) {
        List<String> newSkillNames = skillNames.stream()
                .filter(skillName -> !skillRepository.existsBySkillName(skillName))
                .collect(Collectors.toList());
        List<Skill> newSkills = newSkillNames.stream()
                .map(Skill::new)
                .collect(Collectors.toList());
        skillRepository.saveAll(newSkills);
    }

    @Override
    public Long deleteUserSkills(String userName, List<String> skillNames) {
        long deletedCount = userSkillRepository.deleteBySkill_SkillNameIn(skillNames);
        return deletedCount;

    }

    @Override
    public Long deleteSkills(List<String> skills) {
        long deletedCount = skillRepository.deleteBySkillNameIn(skills);
        return deletedCount;
    }

    @Override
    public List<com.dal.skillswap.entities.Skill> getSkills() {
        return skillRepository.findAll();
    }

    @Override
    public List<com.dal.skillswap.models.response.UserSkill> getUserSkills(String userName) {
        User user = getUser(userName);
        List<com.dal.skillswap.models.response.UserSkill> userSkills =
                user.getUserSkills().stream().map(userSkill -> userSkillMapper.toUserSkillModel(userSkill)).collect(Collectors.toList());

        return userSkills;
    }

    @Override
    public boolean setProfilePicture(String path) throws UsernameNotFoundException {
        User user = getLoggedInUser();
        user.setProfilePictureName(path);
        userRepository.save(user);

        return true;
    }

    @Override
    public String getProfilePicture() throws UsernameNotFoundException {
        User user = getLoggedInUser();
        return user.getProfilePictureName();
    }

    @Override
    public User getLoggedInUser() {
        return getUser(getLoggedInUserEmail());
    }

    @Override
    public String getLoggedInUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (String) authentication.getPrincipal();
    }

    @Override
    public Coordinate getUserCoordinates(String email) {
        User user = getUser(email);
        return locationService.convertLocationToCoordinates(user.getPincode());
    }

    /**
     * Get user object with email
     *
     * @param email
     * @return
     * @throws UsernameNotFoundException
     */
    private User getUser(String email) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }
        return user.get();
    }

    /**
     * Maps a user request to an entity
     *
     * @param user
     * @param userEntity
     * @return
     */
    private User mapUserRequestToEntity(com.dal.skillswap.models.request.User user, User userEntity) {
        if (user == null) {
            return null;
        }

        userEntity.setId(userEntity.getId());
        userEntity.setFirstName(getNonNullString(user.getFirstName(), userEntity.getFirstName()));
        userEntity.setLastName(getNonNullString(user.getLastName(), userEntity.getLastName()));
        userEntity.setMobile(getNonNullString(user.getMobile(), userEntity.getMobile()));
        userEntity.setEmail(getNonNullString(user.getEmail(), userEntity.getEmail()));
        userEntity.setRole((UserRole) getNonNullObject(user.getRole(), userEntity.getRole()));
        userEntity.setPincode(getNonNullString(user.getPincode(), userEntity.getPincode()));
        userEntity.setPassword(getNonNullString(user.getPassword(), userEntity.getPassword()));
        userEntity.setProfilePictureName(userEntity.getProfilePictureName());
        userEntity.setAge(getNonZeroInt(user.getAge(), userEntity.getAge()));
        userEntity.setDescription(getNonNullString(user.getDescription(), userEntity.getDescription()));
        userEntity.setLinkedInLink(getNonNullString(user.getLinkedInLink(), userEntity.getLinkedInLink()));
        userEntity.setYoutubeLink(getNonNullString(user.getYoutubeLink(), userEntity.getYoutubeLink()));
        userEntity.setInstagramLink(getNonNullString(user.getInstagramLink(), userEntity.getInstagramLink()));

        if (user.getEnableNotification() != null) {
            userEntity.setEnableNotification(user.getEnableNotification());
        } else {
            userEntity.setEnableNotification(userEntity.getEnableNotification());
        }

        if (user.getEnableNotificationSounds() != null) {
            userEntity.setEnableNotificationSounds(user.getEnableNotificationSounds());
        } else {
            userEntity.setEnableNotificationSounds(userEntity.getEnableNotificationSounds());
        }
        return userEntity;
    }

    /**
     * Returns a non-null string if the new string is not null or the old string otherwise
     *
     * @param newString
     * @param oldString
     * @return
     */
    private static String getNonNullString(String newString, String oldString) {
        return !StringUtils.isNullOrEmpty(newString) ? newString : oldString;
    }

    /**
     * Returns a non-zero integer if the new integer is not zero or the old integer otherwise
     *
     * @param newNumber
     * @param oldNumber
     * @return
     */
    private int getNonZeroInt(int newNumber, int oldNumber) {
        return newNumber != 0 ? newNumber : oldNumber;
    }

    /**
     * Returns a non-null object if the new object is not null or the old object otherwise
     *
     * @param newObject
     * @param oldObject
     * @return
     */
    private Object getNonNullObject(Object newObject, Object oldObject) {
        return newObject != null ? newObject : oldObject;
    }

    @Override
    public List<com.dal.skillswap.models.response.User> getUsersWithinRange(String loggedInUserEmail, Double rangeKm, Optional<Long> skillId) {
        Coordinate loggedInUserCoordinates = getUserCoordinates(loggedInUserEmail);
        List<User> allUsers = userRepository.findAll();
        Stream<User> nearbyUsersStream = allUsers.stream()
                .filter(user -> !user.getEmail().equals(loggedInUserEmail))
                .filter(user -> {
                    Coordinate userCoordinates = new Coordinate(user.getLatitude(), user.getLongitude());
                    double distance = loggedInUserCoordinates.calculateDistance(userCoordinates);
                    return distance <= rangeKm * MULTIPLICATION_KM_TO_M;
                });
        return skillId.map(id -> nearbyUsersStream
                .filter(user -> user.getUserSkills().stream()
                        .anyMatch(skill -> skill.getSkill().getSkillId().equals(id)))
                .map(userMapper::userEntityToDto)
                .collect(Collectors.toList())).orElseGet(() -> nearbyUsersStream
                .map(userMapper::userEntityToDto)
                .collect(Collectors.toList()));
    }
}

