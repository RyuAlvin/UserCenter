package com.yeahicode.ucbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yeahicode.ucbackend.model.User;
import com.yeahicode.ucbackend.service.UserService;
import com.yeahicode.ucbackend.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
* @author ryualvin
* @description 针对表【user】的数据库操作Service实现
* @createDate 2026-03-18 20:12:58
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

}




