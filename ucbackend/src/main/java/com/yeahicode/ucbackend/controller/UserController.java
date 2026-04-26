package com.yeahicode.ucbackend.controller;

import com.yeahicode.ucbackend.common.BaseResponse;
import com.yeahicode.ucbackend.common.Constants;
import com.yeahicode.ucbackend.common.Result;
import com.yeahicode.ucbackend.common.StatusCode;
import com.yeahicode.ucbackend.exception.BusinessException;
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
    public BaseResponse<Long> userRegister(@RequestBody RegisterUser registerUser) {
        // 1、参数对象非空校验
        if (registerUser == null) {
            throw new BusinessException(StatusCode.PARAM_ERROR, Constants.Param.NULL);
        }
        // 2、参数对象内容非空校验
        String userAccount = registerUser.getUserAccount();
        String userPassword = registerUser.getUserPassword();
        String checkPassword = registerUser.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(StatusCode.PARAM_ERROR, Constants.Param.BLANK);
        }
        // 3、注册
        Long id = userService.userRegister(userAccount, userPassword, checkPassword);

        return Result.success(id);
    }

    @PostMapping("/login")
    public BaseResponse<Void> userLogin(@RequestBody LoginUser loginUser, HttpServletRequest request) {
        // 1、参数对象非空校验
        if (loginUser == null) {
            throw new BusinessException(StatusCode.PARAM_ERROR, Constants.Param.NULL);
        }
        // 2、参数对象内容非空校验
        String userAccount = loginUser.getUserAccount();
        String userPassword = loginUser.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(StatusCode.PARAM_ERROR, Constants.Param.BLANK);
        }
        // 3、登录
        userService.userLogin(userAccount, userPassword, request);

        return Result.success();
    }

    @GetMapping("/current")
    public BaseResponse<LoginedUser> getCurrentUser(HttpServletRequest request) {
        // 1、session非空校验
        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new BusinessException(StatusCode.SESSION_ERROR, Constants.Session.NULL);
        }
        // 2、登录态非空校验
        LoginedUser loginedUser = (LoginedUser) session.getAttribute(Constants.Session.KEY);
        if (loginedUser == null) {
            throw new BusinessException(StatusCode.SESSION_ERROR, Constants.Session.BLANK);
        }
        // 3、从DB中获取实时用户信息
        User rtUser = userService.getById(loginedUser.getId());
        if (rtUser == null) {
            throw new BusinessException(StatusCode.DB_INFO_ERROR, Constants.Biz.NO_USER);
        }
        // 4、最新用户状态校验
        if (rtUser.getUserStatus() == 0) {
            throw new BusinessException(StatusCode.DB_INFO_ERROR, Constants.Biz.DISABLE_USER);
        }
        // 5、脱敏
        BeanUtils.copyProperties(rtUser, loginedUser);

        return Result.success(loginedUser);
    }

    @GetMapping("/list")
    public BaseResponse<List<SearchUser>> getUserList(HttpServletRequest request) {
        List<SearchUser> searchUsers = userService.userList(request);
        return Result.success(searchUsers);
    }

    @PostMapping("/logout")
    public BaseResponse<Void> userLogout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new BusinessException(StatusCode.SESSION_ERROR, Constants.Session.NULL);
        }
        session.removeAttribute(Constants.Session.KEY);

        return Result.success();
    }
}
