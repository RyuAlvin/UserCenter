package com.yeahicode.ucbackend.exception;

import com.yeahicode.ucbackend.common.StatusCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private String desc;

    private StatusCode statusCode;

    public BusinessException(StatusCode statusCode, String desc) {
        super(statusCode.getMsg());
        this.statusCode = statusCode;
        this.desc = desc;
    }
}
