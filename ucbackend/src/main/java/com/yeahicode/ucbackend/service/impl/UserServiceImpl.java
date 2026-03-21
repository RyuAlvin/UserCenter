package com.yeahicode.ucbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yeahicode.ucbackend.model.User;
import com.yeahicode.ucbackend.service.UserService;
import com.yeahicode.ucbackend.mapper.UserMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

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
        // 2、用户名长度校验（避免正则表达式过度匹配）
        if (userAccount.length() < 5 || userAccount.length() > 20) {
            log.error("用户名长度不符合5-20位");
            return -1L;
        }
        // 3、用户名常用规则校验（长度限制，特殊字符限制等）
        // 必须以字母开头，只允许字母、数字、下划线、点，长度5-20位
        // 使用预编译的Pattern
        Pattern pattern = Pattern.compile("^[a-zA-Z][a-zA-Z0-9_.]{4,19}$");
        if (!pattern.matcher(userAccount).matches()) {
            log.error("用户名长度不符合命名规范：必须以字母开头，只允许字母、数字、下划线、点，长度5-20位");
            return -1L;
        }
        // 4、用户名是否存在校验
        // 使用lambda简化
        if (userMapper.exists(new QueryWrapper<User>().lambda().eq(User::getUserAccount, userAccount))) {
            log.error("用户名\"{}\"已存在", userAccount);
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
        newUser.setUserAccount(userAccount);
        newUser.setUserPassword(encryptPassword);
        boolean saved = this.save(newUser);
        if(!saved) {
            log.error("该注册信息保存至数据库时发生错误");
            return -1L;
        }
        // 9、返回id
        log.info("注册成功，id={}", newUser.getId());
        return newUser.getId();
    }
}




