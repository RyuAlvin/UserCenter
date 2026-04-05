package com.yeahicode.ucbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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

import java.util.Collections;
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

    @Resource
    private UserMapper userMapper;

    @Override
    public Long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1、参数非空校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            log.error("存在空参数");
            return -1L;
        }
        // 2、账户长度校验（避免正则表达式过度匹配）
        if (userAccount.length() < 5 || userAccount.length() > 20) {
            log.error("账户长度不符合5-20位");
            return -1L;
        }
        // 3、账户常用规则校验（长度限制，特殊字符限制等）
        // 必须以字母开头，只允许字母、数字、下划线、点，长度5-20位
        // 使用预编译的Pattern
        Pattern pattern = Pattern.compile("^[a-zA-Z][a-zA-Z0-9_.]{4,19}$");
        if (!pattern.matcher(userAccount).matches()) {
            log.error("账户长度不符合命名规范：必须以字母开头，只允许字母、数字、下划线、点，长度5-20位");
            return -1L;
        }
        // 4、账户是否存在校验
        // 使用lambda简化
        if (userMapper.exists(new QueryWrapper<User>().lambda().eq(User::getUserAccount, userAccount))) {
            log.error("账户\"{}\"已存在", userAccount);
            return -1L;
        }
        // 5、密码长度校验（不小于8位）
        if (userPassword.length() < 8) {
            log.error("密码长度小于8位");
            return -1L;
        }
        // 6、确认密码是否一致校验
        if (!userPassword.equals(checkPassword)) {
            log.error("密码和确认密码不一致");
            return -1L;
        }
        // 7、密码加盐加密
        String encryptPassword = DigestUtils.md5DigestAsHex((userPassword + "UserCenter").getBytes());
        // 8、存入DB
        User newUser = new User();
        newUser.setUsername(userAccount);
        newUser.setUserAccount(userAccount);
        newUser.setUserPassword(encryptPassword);
        boolean saved = this.save(newUser);
        if (!saved) {
            log.error("该注册信息保存至数据库时发生错误");
            return -1L;
        }
        // 9、返回id
        log.info("注册成功，id={}", newUser.getId());
        return newUser.getId();
    }

    @Override
    public LoginedUser userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1、参数非空校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            log.error("存在空参数");
            return null;
        }
        // 2、查询用户
        User user = userMapper.selectOne(
                new QueryWrapper<User>().lambda()
                        .eq(User::getUserAccount, userAccount));
        // 3、查询结果非空校验
        if (user == null) {
            log.error("用户登录查询结果为空");
            return null;
        }
        // 4、密码加盐加密
        String encryptPassword = DigestUtils.md5DigestAsHex((userPassword + "UserCenter").getBytes());
        // 5、密码验证
        if (!encryptPassword.equals(user.getUserPassword())) {
            log.error("密码不正确");
            return null;
        }
        // 6、用户状态确认
        if (user.getUserStatus() == 0) {
            log.error("当前用户状态不可用");
            return null;
        }
        // 7、用户信息脱敏
        LoginedUser loginedUser = new LoginedUser();
        BeanUtils.copyProperties(user, loginedUser);
        // 若存在session则返回现有session，若没有则创建新session
        HttpSession session = request.getSession(true);
        // 默认时间30分钟，由tomcat的配置文件（conf/web.xml）决定
        // 也可自己设定，以秒为单位
        session.setAttribute("LOGINED_USER", loginedUser);
        // 8、返回脱敏后的用户信息
        return loginedUser;
    }

    @Override
    public List<SearchUser> userList(HttpServletRequest request) {
        // 1、获取当前session
        // 如果当前没有关联的session则返回null
        HttpSession session = request.getSession(false);
        if (session == null) {
            log.error("session不存在，用户未登录");
            return Collections.emptyList();
        }
        // 2、获取当前用户
        LoginedUser loginedUser = (LoginedUser) session.getAttribute("LOGINED_USER");
        if (loginedUser == null) {
            log.error("session中无用户信息");
            return Collections.emptyList();
        }
        // 3、通过用户账户查询数据库中的实时用户信息
        User latestUserInfo = userMapper.selectOne(new QueryWrapper<User>().lambda().eq(User::getUserAccount, loginedUser.getUserAccount()));
        if (latestUserInfo == null) {
            log.error("该用户不存在");
            return Collections.emptyList();
        }
        // 4、用户状态确认
        if (latestUserInfo.getUserStatus() == 0) {
            log.error("当前用户状态不可用");
            return Collections.emptyList();
        }
        // 5、判断用户全新
        if (latestUserInfo.getUserRole() != 2) {
            log.error("该用户非管理员");
            return Collections.emptyList();
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




