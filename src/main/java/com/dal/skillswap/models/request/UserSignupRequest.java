package com.dal.skillswap.models.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Model class for user signup
 */
@AllArgsConstructor
@Getter
public class UserSignupRequest {
    private String firstName;
    private String lastName;
    private String mobile;
    private String email;
    private String pincode;
    private String password;
}
