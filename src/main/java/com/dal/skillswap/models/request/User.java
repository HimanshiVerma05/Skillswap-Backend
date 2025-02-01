package com.dal.skillswap.models.request;

import com.dal.skillswap.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;

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
    private String password;
    private int age;
    private String description;
    private String linkedInLink;
    private String instagramLink;
    private String youtubeLink;
    private Boolean enableNotification = true;
    private Boolean enableNotificationSounds = true;

    public User() {

    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
