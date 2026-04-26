package com.yeahicode.ucbackend.common;

import com.yeahicode.ucbackend.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;

public class Result {

    private Result() {
        throw new UnsupportedOperationException("Result Class ~");
    }

    public static BaseResponse<Void> success() {
        return new BaseResponse<>(null, StatusCode.SUCCESS, StringUtils.EMPTY);
    }

    public static BaseResponse<Void> success(String desc) {
        return new BaseResponse<>(null, StatusCode.SUCCESS, desc);
    }

    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(data, StatusCode.SUCCESS, StringUtils.EMPTY);
    }

    public static BaseResponse<Void> error(StatusCode sc) {
        return new BaseResponse<>(null, sc, StringUtils.EMPTY);
    }

    public static BaseResponse<Void> error(BusinessException be) {
        return new BaseResponse<>(null, be.getStatusCode(), be.getDesc());
    }
}
