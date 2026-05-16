package com.yeahicode.ucbackend.model.response;

import lombok.Data;

import java.util.Date;

@Data
public class SearchUser {

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
     * 性别
     */
    private Integer gender;

    /**
     * 电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 用户状态（0：禁用、1：可用）
     */
    private Integer userStatus;

    /**
     * 用户角色（1：普通用户、2：管理者）
     */
    private Integer userRole;

    /**
     * 标签
     */
    private String tag;

    /**
     * 逻辑删除
     */
    private Integer isDelete;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
