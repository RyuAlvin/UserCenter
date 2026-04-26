package com.yeahicode.ucbackend.common;

import lombok.Getter;

@Getter
public enum StatusCode {

    SUCCESS(20000, "请求成功"),
    SESSION_ERROR(30000, "登录状态有误"),
    PARAM_ERROR(40000, "输入有误"),
    SYSTEM_ERROR(50000, "系统有误"),
    DB_INFO_ERROR(60000, "数据有误");

    private final int code;

    private final String msg;

    StatusCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
