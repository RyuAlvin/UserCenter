package com.yeahicode.ucbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yeahicode.ucbackend.model.User;
import com.yeahicode.ucbackend.model.response.LoginedUser;
import com.yeahicode.ucbackend.model.response.SearchUser;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author ryualvin
 * @description 针对表【user】的数据库操作Service
 * @createDate 2026-03-18 20:12:58
 */
public interface UserService extends IService<User> {

    Long userRegister(String userAccount, String userPassword, String checkPassword);

    LoginedUser userLogin(String userAccount, String userPassword, HttpServletRequest request);

    List<SearchUser> userList(HttpServletRequest request);
}
