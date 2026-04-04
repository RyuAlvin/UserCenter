package com.yeahicode.ucbackend.model.response;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;

@Data
public class LoginedUser implements Serializable {

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 昵称
     */
    private String username;

    /**
     * 用户的登录账号（用来登录系统的唯一标识，一旦注册，通常不可修改或修改流程非常严格）
     */
    private String userAccount;

    /**
     * 头像
     */
    private String avatarUrl;

    /**
     * 用户状态
     */
    private Integer userStatus;

    /**
     * 用户角色
     */
    private Integer userRole;
}
