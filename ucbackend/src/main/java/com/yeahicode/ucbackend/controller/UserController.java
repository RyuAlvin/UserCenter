package com.yeahicode.ucbackend.controller;

import com.yeahicode.ucbackend.model.User;
import com.yeahicode.ucbackend.model.request.LoginUser;
import com.yeahicode.ucbackend.model.request.RegisterUser;
import com.yeahicode.ucbackend.model.response.LoginedUser;
import com.yeahicode.ucbackend.model.response.SearchUser;
import com.yeahicode.ucbackend.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/current")
    public LoginedUser getCurrentUser(HttpServletRequest request) {
        // 1、session非空校验
        HttpSession session = request.getSession(false);
        if(session == null) {
            log.error("session为空");
            return null;
        }
        // 2、登录态非空校验
        LoginedUser loginedUser = (LoginedUser) session.getAttribute("LOGINED_USER");
        if(loginedUser == null) {
            log.error("session中无登录态");
            return null;
        }
        // 3、从DB中获取实时用户信息
        User rtUser = userService.getById(loginedUser.getId());
        if(rtUser == null) {
            log.error("DB中不存在改用户");
            return null;
        }
        // 4、最新用户状态校验
        if (rtUser.getUserStatus() == 0) {
            log.error("该用户目前为禁用状态");
            return null;
        }
        // 5、脱敏
        BeanUtils.copyProperties(rtUser, loginedUser);
        return loginedUser;
    }

    @GetMapping("/list")
    public List<SearchUser> getUserList(HttpServletRequest request) {
        return userService.userList(request);
    }

    @PostMapping("/logout")
    public Long userLogout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if(session == null) {
            return null;
        }
        session.removeAttribute("LOGINED_USER");
        return 1L;
    }
}
