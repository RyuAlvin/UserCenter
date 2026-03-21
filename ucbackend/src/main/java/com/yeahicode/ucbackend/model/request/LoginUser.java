package com.yeahicode.ucbackend.model.request;

import lombok.Data;

@Data
public class LoginUser {
    private String userAccount;

    private String userPassword;
}
