package com.liang.usercenter.model;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 *
 * @TableName user
 */
@TableName(value ="user")
@Data
public class User implements Serializable {
    /**
     * 用户id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户昵称
     */
    private String username;

    /**
     * 用户账号
     */
    private String userAccount;

    /**
     * 用户头像
     */
    private String avatarUrl;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 密码
     */
    private String passwd;

    /**
     * 电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer deleted;

    /**
     * 用户身份，0普通用户，1管理员
     */
    private Integer roles;

    /**
     * 用户标签
     */
    private String tags;

    /**
     * 用户介绍
     */
    private String introduction;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}