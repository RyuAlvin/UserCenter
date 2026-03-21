package com.yeahicode.ucbackend.controller;

import com.yeahicode.ucbackend.model.request.LoginUser;
import com.yeahicode.ucbackend.model.request.RegisterUser;
import com.yeahicode.ucbackend.model.response.LoginedUser;
import com.yeahicode.ucbackend.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/user")
@RestController
@Slf4j
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/register")
    public Long userRegister(@RequestBody RegisterUser registerUser) {
        // 1、参数对象非空校验
        if (registerUser == null) {
            log.error("参数对象为空");
            return -1L;
        }
        // 2、参数对象内容非空校验
        String userAccount = registerUser.getUserAccount();
        String userPassword = registerUser.getUserPassword();
        String checkPassword = registerUser.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            log.error("存在空参数");
            return -1L;
        }
        // 3、注册
        return userService.userRegister(userAccount, userPassword, checkPassword);
    }

    @PostMapping("/login")
    public LoginedUser userLogin(@RequestBody LoginUser loginUser, HttpServletRequest request) {
        // 1、参数对象非空校验
        if (loginUser == null) {
            log.error("参数对象为空");
            return null;
        }
        // 2、参数对象内容非空校验
        String userAccount = loginUser.getUserAccount();
        String userPassword = loginUser.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            log.error("存在空参数");
            return null;
        }
        // 3、登录
        return userService.userLogin(userAccount, userPassword, request);
    }
}
