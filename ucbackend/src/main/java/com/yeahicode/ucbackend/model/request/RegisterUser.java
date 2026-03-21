package com.yeahicode.ucbackend.model.request;

import lombok.Data;

@Data
public class RegisterUser {

    private String userAccount;

    private String userPassword;

    private String checkPassword;
}
