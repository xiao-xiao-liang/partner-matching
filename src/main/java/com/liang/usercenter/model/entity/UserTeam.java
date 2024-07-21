package com.liang.usercenter.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;

import lombok.Builder;
import lombok.Data;

/**
 * 用户-队伍关系
 * @TableName user_team
 */
@TableName(value ="user_team")
@Data
@Builder
public class UserTeam implements Serializable {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 队伍id
     */
    @TableField(value = "team_id")
    private Long teamId;

    /**
     * 用户名
     */
    @TableField(value = "username")
    private String username;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer deleted;

    /**
     * 加入时间
     */
    @TableField(value = "join_time")
    private Date joinTime;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}