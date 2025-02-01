package com.dal.skillswap.models.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ForgotPasswordRequest {
    private String email;

    public ForgotPasswordRequest() {
    }
}
