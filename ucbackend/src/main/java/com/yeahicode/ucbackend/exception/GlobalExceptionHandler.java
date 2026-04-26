package com.yeahicode.ucbackend.exception;

import com.yeahicode.ucbackend.common.BaseResponse;
import com.yeahicode.ucbackend.common.Result;
import com.yeahicode.ucbackend.common.StatusCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {BusinessException.class})
    public BaseResponse<Void> handleBusinessException(BusinessException be) {
        log.error(be.getMessage());
        return Result.error(be);
    }

    @ExceptionHandler(value = {Exception.class})
    public BaseResponse<Void> handleSystemException(Exception e) {
        log.error(e.getMessage());
        return Result.error(StatusCode.SYSTEM_ERROR);
    }
}
