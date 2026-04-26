package com.yeahicode.ucbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yeahicode.ucbackend.common.Constants;
import com.yeahicode.ucbackend.common.StatusCode;
import com.yeahicode.ucbackend.exception.BusinessException;
import com.yeahicode.ucbackend.mapper.UserMapper;
import com.yeahicode.ucbackend.model.User;
import com.yeahicode.ucbackend.model.response.LoginedUser;
import com.yeahicode.ucbackend.model.response.SearchUser;
import com.yeahicode.ucbackend.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.List;
import java.util.regex.Pattern;

/**
 * @author ryualvin
 * @description 针对表【user】的数据库操作Service实现
 * @createDate 2026-03-18 20:12:58
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    private static final String USER_RULE = "^[a-zA-Z][a-zA-Z0-9_.]{4,19}$";

    private static final String ENCRYPT_SALT = "UserCenter";

    @Resource
    private UserMapper userMapper;

    @Override
    public Long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1、参数非空校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(StatusCode.PARAM_ERROR, Constants.Param.BLANK);
        }
        // 2、账户长度校验（避免正则表达式过度匹配）
        if (userAccount.length() < 5 || userAccount.length() > 20) {
            throw new BusinessException(StatusCode.PARAM_ERROR, Constants.Param.USER_LEN_ERR);
        }
        // 3、账户常用规则校验（长度限制，特殊字符限制等）
        // 必须以字母开头，只允许字母、数字、下划线、点，长度5-20位
        // 使用预编译的Pattern
        Pattern pattern = Pattern.compile(USER_RULE);
        if (!pattern.matcher(userAccount).matches()) {
            throw new BusinessException(StatusCode.PARAM_ERROR, Constants.Param.USER_RULE_ERR);
        }
        // 4、账户是否存在校验
        // 使用lambda简化
        if (userMapper.exists(new QueryWrapper<User>().lambda().eq(User::getUserAccount, userAccount))) {
            throw new BusinessException(StatusCode.PARAM_ERROR, Constants.Biz.REPEAT_USER);
        }
        // 5、密码长度校验（不小于8位）
        if (userPassword.length() < 8) {
            throw new BusinessException(StatusCode.PARAM_ERROR, Constants.Param.PASS_LEN_ERR);
        }
        // 6、确认密码是否一致校验
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(StatusCode.PARAM_ERROR, Constants.Param.CHECK_PASS_ERR);
        }
        // 7、密码加盐加密
        String encryptPassword = DigestUtils.md5DigestAsHex((userPassword + ENCRYPT_SALT).getBytes());
        // 8、存入DB
        User newUser = new User();
        newUser.setUsername(userAccount);
        newUser.setUserAccount(userAccount);
        newUser.setUserPassword(encryptPassword);
        boolean saved = this.save(newUser);
        if (!saved) {
            throw new BusinessException(StatusCode.PARAM_ERROR, Constants.Biz.SAVE_ERROR);
        }
        // 9、返回id
        log.info(Constants.Log.REGISTER_SUCCESS, newUser.getId());
        return newUser.getId();
    }

    @Override
    public LoginedUser userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1、参数非空校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(StatusCode.PARAM_ERROR, Constants.Param.BLANK);
        }
        // 2、查询用户
        User user = userMapper.selectOne(
                new QueryWrapper<User>().lambda()
                        .eq(User::getUserAccount, userAccount));
        // 3、查询结果非空校验
        if (user == null) {
            throw new BusinessException(StatusCode.PARAM_ERROR, Constants.Biz.NO_USER);
        }
        // 4、密码加盐加密
        String encryptPassword = DigestUtils.md5DigestAsHex((userPassword + ENCRYPT_SALT).getBytes());
        // 5、密码验证
        if (!encryptPassword.equals(user.getUserPassword())) {
            throw new BusinessException(StatusCode.PARAM_ERROR, Constants.Biz.WRONG_PASS);
        }
        // 6、用户状态确认
        if (user.getUserStatus() == 0) {
            throw new BusinessException(StatusCode.DB_INFO_ERROR, Constants.Biz.DISABLE_USER);
        }
        // 7、用户信息脱敏
        LoginedUser loginedUser = new LoginedUser();
        BeanUtils.copyProperties(user, loginedUser);
        // 若存在session则返回现有session，若没有则创建新session
        HttpSession session = request.getSession(true);
        // 默认时间30分钟，由tomcat的配置文件（conf/web.xml）决定
        // 也可自己设定，以秒为单位
        session.setAttribute(Constants.Session.KEY, loginedUser);
        // 8、返回脱敏后的用户信息
        return loginedUser;
    }

    @Override
    public List<SearchUser> userList(HttpServletRequest request) {
        // 1、获取当前session
        // 如果当前没有关联的session则返回null
        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new BusinessException(StatusCode.DB_INFO_ERROR, Constants.Session.NULL);
        }
        // 2、获取当前用户
        LoginedUser loginedUser = (LoginedUser) session.getAttribute(Constants.Session.KEY);
        if (loginedUser == null) {
            throw new BusinessException(StatusCode.DB_INFO_ERROR, Constants.Session.BLANK);
        }
        // 3、通过用户账户查询数据库中的实时用户信息
        User latestUserInfo = userMapper.selectOne(new QueryWrapper<User>().lambda().eq(User::getUserAccount, loginedUser.getUserAccount()));
        if (latestUserInfo == null) {
            throw new BusinessException(StatusCode.DB_INFO_ERROR, Constants.Biz.NO_USER);
        }
        // 4、用户状态确认
        if (latestUserInfo.getUserStatus() == 0) {
            throw new BusinessException(StatusCode.DB_INFO_ERROR, Constants.Biz.DISABLE_USER);
        }
        // 5、判断用户全新
        if (latestUserInfo.getUserRole() != 2) {
            throw new BusinessException(StatusCode.DB_INFO_ERROR, Constants.Biz.NO_ADMIN);
        }
        // 6、查询所有用户
        List<User> userList = userMapper.selectAllIncludingDeleted();
        // 7、循环数据脱敏
        return userList.stream().map(user -> {
            SearchUser searchUser = new SearchUser();
            BeanUtils.copyProperties(user, searchUser);
            return searchUser;
        }).toList();
    }
}




