package com.dal.skillswap.service.impl;

import com.dal.skillswap.entities.Skill;
import com.dal.skillswap.entities.User;
import com.dal.skillswap.enums.UserRole;
import com.dal.skillswap.mapper.UserMapper;
import com.dal.skillswap.mapper.UserSkillMapper;
import com.dal.skillswap.models.response.UserSkill;
import com.dal.skillswap.models.util.Coordinate;
import com.dal.skillswap.repository.SkillRepository;
import com.dal.skillswap.repository.UserRepository;
import com.dal.skillswap.repository.UserSkillRepository;
import com.dal.skillswap.service.LocationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class UserServiceImplTests {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private LocationService locationService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserSkillRepository userSkillRepository;
    @Mock
    private SkillRepository skillRepository;

    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);
    @Spy
    private UserSkillMapper userSkillMapper = Mappers.getMapper(UserSkillMapper.class);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }


    @Test
    void testGetUserDetails() {
        User user = getSampleUserEntity();
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        com.dal.skillswap.models.response.User userResponse = userService.getUserDetails("test");
        assertEquals(userResponse.getFirstName(), "test");
    }

    @Test
    void testGetNonExistingUserDetails() {
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> userService.getUserDetails("test"));
    }

    @Test
    void testSetProfilePicture() {
        User user = getSampleUserEntity();
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        assertTrue(userService.setProfilePicture("test"));
    }

    @Test
    void testSetNonExistingUserProfilePicture() {
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> userService.setProfilePicture("test"));
    }

    @Test
    void testGetProfilePicture() {
        User user = getSampleUserEntity();
        user.setProfilePictureName("testFile");
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        assertEquals(userService.getProfilePicture(), "testFile");
    }

    @Test
    void testGetUserSkills() {
        User user = getSampleUserEntity();
        List<com.dal.skillswap.entities.UserSkill> userSkills = new ArrayList<>();
        userSkills.add(new com.dal.skillswap.entities.UserSkill(null, new Skill("skill1")));
        userSkills.add(new com.dal.skillswap.entities.UserSkill(null, new Skill("skill2")));
        user.setUserSkills(userSkills);

        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        List<UserSkill> userSkillsResponse = userService.getUserSkills(user.getUsername());
        assertEquals(userSkillsResponse.size(), 2);
    }

    @Test
    void testAddUserSkills() {
        User user = getSampleUserEntity();
        List<com.dal.skillswap.entities.UserSkill> userSkills = new ArrayList<>();
        userSkills.add(new com.dal.skillswap.entities.UserSkill(user, new Skill("skill1")));

        user.setUserSkills(userSkills);
        List<String> skillNames = Arrays.asList("skill2", "skill3");

        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        userService.addUserSkills(user.getUsername(), skillNames);

        ArgumentCaptor<Skill> skillRepoCaptor = ArgumentCaptor.forClass(Skill.class);
        verify(skillRepository, times(2)).save(skillRepoCaptor.capture());
        com.dal.skillswap.entities.Skill saveSkillsArguments = skillRepoCaptor.getValue();
        assertEquals("skill3", saveSkillsArguments.getSkillName());

        ArgumentCaptor<List<com.dal.skillswap.entities.UserSkill>> userSkillRepoCaptor = ArgumentCaptor.forClass(List.class);
        verify(userSkillRepository, times(1)).saveAll(userSkillRepoCaptor.capture());

     /*  List<com.dal.skillswap.entities.UserSkill> saveAllUserSkillsArguments = userSkillRepoCaptor.getValue();
        assertEquals("skill1", saveAllUserSkillsArguments.get(0).getSkill().getSkillName());*/

    }

    @Test
    void testGetSkills() {
        List<com.dal.skillswap.entities.Skill> skills = new ArrayList<>();
        skills.add(new Skill("skill1"));
        skills.add(new Skill("skill2"));

        when(skillRepository.findAll()).thenReturn(skills);

        List<Skill> skillsResponse = userService.getSkills();
        assertEquals(skillsResponse.size(), 2);
    }

    @Test
    void testAddSkills() {
        List<String> skillNames = Arrays.asList("skill2", "skill3");

        userService.addSkills(skillNames);
        ArgumentCaptor<List<Skill>> skillRepoCaptor = ArgumentCaptor.forClass(List.class);
        verify(skillRepository, times(1)).saveAll(skillRepoCaptor.capture());
        assertEquals("skill2", skillRepoCaptor.getValue().get(0).getSkillName());
    }

    @Test
    void deleteSkills() {
        List<String> skillNames = Arrays.asList("skill1", "skill2");
        userService.deleteSkills(skillNames);
        ArgumentCaptor<List<String>> skillRepoCaptor = ArgumentCaptor.forClass(List.class);
        verify(skillRepository, times(1)).deleteBySkillNameIn(skillRepoCaptor.capture());
        assertEquals(skillNames, skillRepoCaptor.getValue());

    }

    @Test
    void deleteUserSkills() {
        User user = getSampleUserEntity();
        List<com.dal.skillswap.entities.UserSkill> userSkills = new ArrayList<>();
        userSkills.add(new com.dal.skillswap.entities.UserSkill(null, new Skill("skill1")));
        userSkills.add(new com.dal.skillswap.entities.UserSkill(null, new Skill("skill2")));
        userSkills.add(new com.dal.skillswap.entities.UserSkill(null, new Skill("skill3")));
        user.setUserSkills(userSkills);

        List<String> skillNames = Arrays.asList("skill2");
        userService.deleteUserSkills(user.getUsername(), skillNames);

        ArgumentCaptor<List<String>> skillRepoCaptor = ArgumentCaptor.forClass(List.class);
        verify(userSkillRepository, times(1)).deleteBySkill_SkillNameIn(skillRepoCaptor.capture());
        assertEquals(skillRepoCaptor.getValue().get(0), "skill2");
    }

    @Test
    void testGetUserCoordinates() {
        User user = getSampleUserEntity();
        Coordinate coordinate = new Coordinate(44.629021, -63.5714195);
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(locationService.convertLocationToCoordinates(any())).thenReturn(coordinate);
        assertEquals(userService.getUserCoordinates(""), coordinate);
    }

    @Test
    void testGetUsersWithinRange() {

        String loggedInUserEmail = "test123@example.com";
        double rangeKm = 10.0;
        Optional<Long> skillId = Optional.empty();

        User mockUser = new User();
        mockUser.setEmail(loggedInUserEmail);
        when(userRepository.findByEmail(loggedInUserEmail)).thenReturn(Optional.of(mockUser));

        Coordinate loggedInUserCoordinates = new Coordinate(44.648764, -63.575239);
        when(userService.getUserCoordinates(loggedInUserEmail)).thenReturn(loggedInUserCoordinates);

        User user1 = new User();
        user1.setEmail("user1@example.com");
        user1.setLatitude(44.651070);
        user1.setLongitude(-63.582687);

        User user2 = new User();
        user2.setEmail("user2@example.com");
        user2.setLatitude(45.963589);
        user2.setLongitude(-66.643115);

        List<User> allUsers = new ArrayList<>();
        allUsers.add(user1);
        allUsers.add(user2);

        when(userRepository.findAll()).thenReturn(allUsers);
        Coordinate coordinateInvocation = new Coordinate(
                user1.getLatitude(),
                user1.getLongitude()
        );
        when(locationService.convertLocationToCoordinates(anyString()))
                .thenAnswer(invocation -> coordinateInvocation);

        List<com.dal.skillswap.models.response.User> usersWithinRange = userService.getUsersWithinRange(loggedInUserEmail, rangeKm, skillId);
        assertEquals(user1.getEmail(), usersWithinRange.get(0).getEmail());

        verify(userRepository, times(1)).findAll();
        verify(locationService, times(1)).convertLocationToCoordinates(user1.getPincode());
        verify(locationService, times(1)).convertLocationToCoordinates(user2.getPincode());
    }

    @Test
    void testGetUsersOutsideRange() {

        String loggedInUserEmail = "test@example.com";
        double rangeKm = 5.0;

        User mockUser = new User();
        mockUser.setEmail(loggedInUserEmail);
        when(userRepository.findByEmail(loggedInUserEmail)).thenReturn(Optional.of(mockUser));

        Coordinate loggedInUserCoordinates = new Coordinate(44.648764, -63.575239);
        when(userService.getUserCoordinates(loggedInUserEmail)).thenReturn(loggedInUserCoordinates);

        User userFar1 = new User();
        userFar1.setEmail("faruser1@example.com");
        userFar1.setLatitude(45.963589); // Far away user's latitude
        userFar1.setLongitude(-66.643115); // Far away user's longitude

        User userFar2 = new User();
        userFar2.setEmail("faruser2@example.com");
        userFar2.setLatitude(46.215220); // Another far away user's latitude
        userFar2.setLongitude(-63.131070); // Another far away user's longitude

        List<User> allUsers = new ArrayList<>();
        allUsers.add(userFar1);
        allUsers.add(userFar2);

        when(userRepository.findAll()).thenReturn(allUsers);

        when(locationService.convertLocationToCoordinates(anyString()))
                .thenAnswer(invocation -> new Coordinate(
                        ((User) invocation.getArgument(0)).getLatitude(),
                        ((User) invocation.getArgument(0)).getLongitude()
                ));

        List<com.dal.skillswap.models.response.User> usersWithinRange =
                userService.getUsersWithinRange(loggedInUserEmail, rangeKm, Optional.empty());

        assertTrue(usersWithinRange.isEmpty(), "No users within 5 km range");

        verify(userRepository).findAll();
    }

    @Test
    void testGetUsersWithinRangeWithSkillId() {
        // Arrange
        String loggedInUserEmail = "test@example.com";
        Double rangeKm = 10.0;
        Long skillId = 1L;

        User mockUser = new User();
        mockUser.setEmail(loggedInUserEmail);
        when(userRepository.findByEmail(loggedInUserEmail)).thenReturn(Optional.of(mockUser));

        Coordinate loggedInUserCoordinates = new Coordinate(44.648764, -63.575239);
        when(userService.getUserCoordinates(loggedInUserEmail)).thenReturn(loggedInUserCoordinates);

        User userFar1 = new User();
        userFar1.setEmail("faruser1@example.com");
        userFar1.setLatitude(45.963589); // Far away user's latitude
        userFar1.setLongitude(-66.643115); // Far away user's longitude

        User userFar2 = new User();
        userFar2.setEmail("faruser2@example.com");
        userFar2.setLatitude(46.215220); // Another far away user's latitude
        userFar2.setLongitude(-63.131070); // Another far away user's longitude

        List<User> allUsers = new ArrayList<>();
        allUsers.add(userFar1);
        allUsers.add(userFar2);

        when(userRepository.findAll()).thenReturn(allUsers);

        List<com.dal.skillswap.models.response.User> result = userService.getUsersWithinRange(loggedInUserEmail, rangeKm, Optional.of(skillId));

        List<User> expected = allUsers.stream()
                .filter(user -> !user.getEmail().equals(loggedInUserEmail))
                .filter(user -> {
                    Coordinate userCoordinates = new Coordinate(user.getLatitude(), user.getLongitude());
                    double distance = loggedInUserCoordinates.calculateDistance(userCoordinates);
                    return distance <= rangeKm * 1000;
                })
                .filter(user -> user.getUserSkills().stream()
                        .anyMatch(skill -> skill.getSkill().getSkillId().equals(skillId)))
                .collect(Collectors.toList());

        assertEquals(expected, result);
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testUpdateUserWithDisableMessageNotifications() {
        com.dal.skillswap.models.request.User requestUser = getSampleUserRequest();
        requestUser.setEnableNotification(false);
        User existingUser = getSampleUserEntity();
        existingUser.setEnableNotification(false);
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(existingUser));

        boolean isUpdated = userService.updateUserDetails(requestUser);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertTrue(isUpdated && !userCaptor.getValue().getEnableNotification());
    }

    @Test
    void testUpdateUserWithDisableNotificationSounds() {

        com.dal.skillswap.models.request.User requestUser = getSampleUserRequest();
        requestUser.setEnableNotificationSounds(false);
        User existingUser = getSampleUserEntity();
        existingUser.setEnableNotificationSounds(false);
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(existingUser));

        boolean isUpdated = userService.updateUserDetails(requestUser);
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertTrue(isUpdated && !userCaptor.getValue().getEnableNotificationSounds());

    }

    @Test
    void testUpdateUserDetails() {

        com.dal.skillswap.models.request.User requestUser = getSampleUserRequest();
        User existingUser = getSampleUserEntity();
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(existingUser));

        boolean isUpdated = userService.updateUserDetails(requestUser);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User updatedUser = userCaptor.getValue();

        assertTrue(isUpdated && requestUser.getFirstName().equals(updatedUser.getFirstName()));
    }

    private User getSampleUserEntity() {
        return new User("test", "test", "test@test.com", "", UserRole.USER, "B3H1B9",
                "test123", 0.0, 0.0);
    }

    private com.dal.skillswap.models.request.User getSampleUserRequest() {
        return new com.dal.skillswap.models.request.User(1L, "test", "test", "test@test.com", "", UserRole.USER, "B3H1B9",
                "test123", 20, null, null, null, null, true, true);
    }

}
