package com.yeahicode.ucbackend.common;

import lombok.Data;

import java.io.Serializable;

@Data
public class BaseResponse<T> implements Serializable {

    /**
     * // 第一次：没有手写 serialVersionUID
     * public class User implements Serializable {
     * private String name;
     * private int age;
     * }
     * // JVM 自动生成 UID = 12345
     * <p>
     * // 序列化保存了 User 对象
     * <p>
     * // 后来你加了一个字段
     * public class User implements Serializable {
     * private String name;
     * private int age;
     * private String email;  // 新增字段
     * }
     * // JVM 自动生成 UID = 67890（变了！）
     * <p>
     * // 反序列化旧数据时 → InvalidClassException
     */
    private static final long serialVersionUID = 1L;

    private T data;

    private int code;

    private String msg;

    private String desc;

    public BaseResponse(T data, int code, String msg, String desc) {
        this.data = data;
        this.code = code;
        this.msg = msg;
        this.desc = desc;
    }

    public BaseResponse(T data, StatusCode statusCode, String desc) {
        this(data, statusCode.getCode(), statusCode.getMsg(), desc);
    }
}
