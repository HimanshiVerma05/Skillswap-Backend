package com.dal.skillswap.models.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChangePasswordRequest {
    private String token;
    private String password;

    public ChangePasswordRequest() {
    }
}
