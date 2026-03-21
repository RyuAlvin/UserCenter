package com.yeahicode.ucbackend.service.impl;

import com.yeahicode.ucbackend.model.response.LoginedUser;
import com.yeahicode.ucbackend.model.response.SearchUser;
import com.yeahicode.ucbackend.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@SpringBootTest
class UserServiceImplTest {

    @Resource
    private UserService userService;

    @Test
    void userListTest() {
        List<SearchUser> searchUsers = userService.userList(null);
        searchUsers.forEach(System.out::println);
    }

    @Test
    void userLoginTestUserStatusError() {
        LoginedUser loginedUser = userService.userLogin("Test002", "12345678", null);
        Assertions.assertNull(loginedUser);
    }

    @Test
    void userLoginTestPasswordError() {
        LoginedUser loginedUser = userService.userLogin("Test001", "12345679", null);
        Assertions.assertNull(loginedUser);
    }

    @Test
    void userLoginTestUserExist() {
        LoginedUser loginedUser = userService.userLogin("test000", "abc", null);
        Assertions.assertNull(loginedUser);
    }

    @Test
    void userLoginTestParamsNotBlank() {
        // 创建测试数据流
        Stream<Object[]> stream = Stream.of(
                new Object[]{null, "abc", null},
                new Object[]{"", "abc", null},
                new Object[]{"abc", null, null},
                new Object[]{"abc", "", null}
        );

        // 执行测试
        boolean allMatch = stream.map(params -> {
            return userService.userLogin((String) params[0], (String) params[1], (HttpServletRequest) params[2]);
        }).allMatch(Objects::isNull);

        // 断言
        Assertions.assertTrue(allMatch);
    }

    @Test
    void userRegisterTestRegisterSuccess() {
        Long result = userService.userRegister("alvin11", "12345678", "12345678");
        Assertions.assertTrue(result > -1);
    }

    @Test
    void userRegisterTestCheckPassword() {
        Long result = userService.userRegister("alvinabcde", "12345678", "1234567");
        Assertions.assertEquals(-1, result.longValue());
    }

    @Test
    void userRegisterTestUserPasswordLength() {
        Long result = userService.userRegister("alvin12345", "1234567", "1234567");
        Assertions.assertEquals(-1, result.longValue());
    }

    @Test
    void userRegisterTestUserExist() {
        Long result = userService.userRegister("ryuuu", "abc", "abc");
        Assertions.assertEquals(-1, result.longValue());
    }

    @Test
    void userRegisterTestUserAccountNamingRule() {
        // 创建测试数据流
        Stream<String[]> stream = Stream.of(
                new String[]{"1ab._", "abc", "abc"},
                new String[]{"a1b._@", "abc", "abc"}
        );

        executeServiceMethod(stream);
    }

    @Test
    void userRegisterTestUserAccountLength() {
        // 创建测试数据流
        Stream<String[]> stream = Stream.of(
                new String[]{"1234", "abc", "abc"},
                new String[]{"012345678901234567890", "abc", "abc"}
        );

        executeServiceMethod(stream);
    }

    @Test
    void userRegisterTestParamsNotBlank() {
        // 创建测试数据流
        Stream<String[]> stream = Stream.of(
                new String[]{null, "abc", "abc"},
                new String[]{"", "abc", "abc"},
                new String[]{"abc", null, "abc"},
                new String[]{"abc", "", "abc"},
                new String[]{"abc", "abc", null},
                new String[]{"abc", "abc", ""}
        );

        // 执行测试
        executeServiceMethod(stream);
    }

    private void executeServiceMethod(Stream<String[]> stream) {
        // 执行测试
        boolean allMatch = stream.map(params -> {
            return userService.userRegister(params[0], params[1], params[2]);
        }).allMatch(result -> result == -1);

        // 断言
        Assertions.assertTrue(allMatch);
    }
}