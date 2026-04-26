package com.yeahicode.ucbackend.common;

public final class Constants {

    private Constants() {
    }

    public static final class Param {
        private Param() {
        }

        public static final String NULL = "参数对象为空";
        public static final String BLANK = "存在空参数";
        public static final String USER_LEN_ERR = "账户长度不符合5-20位";
        public static final String USER_RULE_ERR = "账户长度不符合命名规范：必须以字母开头，只允许字母、数字、下划线、点，长度5-20位";
        public static final String PASS_LEN_ERR = "密码长度小于8位";
        public static final String CHECK_PASS_ERR = "密码和确认密码不一致";
    }

    public static final class Session {
        private Session() {
        }

        public static final String KEY = "LOGIN_USER";
        public static final String NULL = "SESSION为空";
        public static final String BLANK = "SESSION中无登录状态";
    }

    public static final class Biz {
        private Biz() {
        }

        public static final String NO_USER = "不存在该用户";
        public static final String DISABLE_USER = "该用户目前为禁用状态";
        public static final String REPEAT_USER = "该账户已存在";
        public static final String SAVE_ERROR = "保存数据时发生错误";
        public static final String WRONG_PASS = "密码不正确";
        public static final String NO_ADMIN = "该用户非管理员";
        public static final String LOGIN_SUCCESS = "登录成功";
        public static final String REGISTER_SUCCESS = "注册成功";
    }

    public static final class Log {
        private Log() {
        }

        public static final String REGISTER_SUCCESS = "注册成功，id={}";
    }
}
