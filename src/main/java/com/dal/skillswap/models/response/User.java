package com.dal.skillswap.models.response;

import com.dal.skillswap.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class User {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String mobile;
    private UserRole role;
    private String pincode;
    private Boolean isVerified;
    private String profilePictureName;
    private int age;
    private String description;
    private String linkedInLink;
    private String instagramLink;
    private String youtubeLink;
    private Boolean enableNotification;
    private Boolean enableNotificationSounds;
    private List<UserSkill> userSkills;
    private Double latitude;
    private Double longitude;
}