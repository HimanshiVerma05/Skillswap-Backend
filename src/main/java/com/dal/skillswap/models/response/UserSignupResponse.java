package com.dal.skillswap.models.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserSignupResponse {
    private String email;
    private String token;
}
