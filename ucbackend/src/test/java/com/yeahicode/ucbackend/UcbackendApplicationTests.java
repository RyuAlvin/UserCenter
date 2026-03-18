package com.yeahicode.ucbackend;

import com.yeahicode.ucbackend.mapper.UserMapper;
import com.yeahicode.ucbackend.model.User;
import com.yeahicode.ucbackend.service.UserService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UcbackendApplicationTests {

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserService userService;

    @Test
    void test02() {
        User user = userService.getById(1);
        System.out.println(user);
    }

    @Test
    void test01() {
        User user = userMapper.selectById(1);
        System.out.println(user);
    }
}
