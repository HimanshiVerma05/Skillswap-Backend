package com.dal.skillswap.controller;

import com.dal.skillswap.mapper.SkillMapper;
import com.dal.skillswap.mapper.UserMapper;
import com.dal.skillswap.models.request.User;
import com.dal.skillswap.models.response.ErrorResponse;
import com.dal.skillswap.models.response.Skill;
import com.dal.skillswap.models.response.UserSkill;
import com.dal.skillswap.models.util.Coordinate;
import com.dal.skillswap.service.FileStoreService;
import com.dal.skillswap.service.UserService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.dal.skillswap.constants.PathConstants.PROFILE_IMAGES;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    FileStoreService fileStoreService;

    @Autowired
    private SkillMapper skillMapper;

    @Autowired
    private UserMapper userMapper;


    @GetMapping("/details")
    public ResponseEntity getUserDetails(@RequestParam(required = false) String email) {
        String user = email;
        if (email == null || email.isEmpty()) {
            user = userService.getLoggedInUserEmail();
        }
        return ResponseEntity.ok(userService.getUserDetails(user));
    }

    @ResponseBody
    @PostMapping("/details/update")
    public ResponseEntity getUserDetails(@RequestBody User user) {
        try {
            return ResponseEntity.ok(userService.updateUserDetails(user));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
        }
    }

    //upload a file
    @PostMapping("/profilePicture/save")
    public ResponseEntity handleFileUpload(@RequestParam("file") MultipartFile file) {
        try {
            return ResponseEntity.ok(fileStoreService.store(file, PROFILE_IMAGES));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
        }
    }

    @GetMapping("/profilePicture/{filename:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        Resource file = fileStoreService.load(filename, PROFILE_IMAGES);
        String fileName = file.getFilename();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""
                        + fileName + "\"").body(file);
    }

    @GetMapping("/test")
    public String test() {
        log.info("testing SKILLSWAP 123 !");
        return "Hello World";
    }

    @PostMapping("/addUserSkills")
    public ResponseEntity addUserSkills(@RequestBody List<String> userSkills) {

        String loggedUser = userService.getLoggedInUserEmail();
        try {
            userService.addUserSkills(loggedUser, userSkills);
        } catch (Exception e) {
            log.info("error in adding skills for the user {}", loggedUser);
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
        return ResponseEntity.ok("Successfully added user skills");
    }

    @PostMapping("/addSkills")
    public ResponseEntity addSkills(@RequestBody List<String> skills) {

        String loggedUser = userService.getLoggedInUserEmail();
        try {
            userService.addSkills(skills);
        } catch (Exception e) {
            log.info("error in adding skills for the user {}", loggedUser);
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
        return ResponseEntity.ok("Successfully added user skills");
    }

    @GetMapping("/getUserSkills")
    public ResponseEntity<List<UserSkill>> getUserSkills(@RequestParam(required = false) String email) {
        String user = email;
        if (email == null || email.isEmpty()) {
            user = userService.getLoggedInUserEmail();
        }
        return ResponseEntity.ok(userService.getUserSkills(user));
    }

    @GetMapping("/getSkills")
    public ResponseEntity<List<Skill>> getSkills() {
        List<Skill> skills = userService.getSkills().stream().map(skill -> skillMapper.toSkillModel(skill)).collect(Collectors.toList());
        return ResponseEntity.ok(skills);
    }

    @DeleteMapping("/deleteUserSkill")
    @Transactional
    public ResponseEntity deleteUserSkills(@RequestBody List<String> skills) {
        String loggedUser = userService.getLoggedInUserEmail();
        Long deletedCount = userService.deleteUserSkills(loggedUser, skills);
        if (deletedCount == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No user skills found with the provided skill names");
        }
        return ResponseEntity.status(HttpStatus.OK).body("User skills deleted successfully");
    }

    @DeleteMapping("/deleteSkill")
    @Transactional
    public ResponseEntity deleteSkill(@RequestBody List<String> skills) {
        Long deletedCount = userService.deleteSkills(skills);
        if (deletedCount == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No user skills found with the provided skill names");
        }
        return ResponseEntity.status(HttpStatus.OK).body("User skills deleted successfully");
    }

    @GetMapping("/userName")
    public ResponseEntity<String> getUserName() {
        String user = userService.getLoggedInUserEmail();
        return ResponseEntity.ok(user);
    }

    @GetMapping("/getCoordinates")
    public ResponseEntity<Coordinate> convertLocationToCoordinate(@RequestParam(required = false) String email) {
        String userEmail = email;
        if (email == null || email.isEmpty()) {
            userEmail = userService.getLoggedInUserEmail();
        }

        return ResponseEntity.ok(userService.getUserCoordinates(userEmail));
    }

    @GetMapping("/getUsersByLocationAndSkillId")
    public ResponseEntity<List<com.dal.skillswap.models.response.User>> getNearbyUsers(@RequestParam Double range, @RequestParam Optional<Long> skillId) {
        String user = userService.getLoggedInUserEmail();
        List<com.dal.skillswap.models.response.User> nearbyUsers = userService.getUsersWithinRange(user, range, skillId);

        return ResponseEntity.ok(nearbyUsers);
    }

}