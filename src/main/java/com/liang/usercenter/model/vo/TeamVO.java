package com.liang.usercenter.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamVO implements Serializable {
    // id
    private Long id;
    // 队伍名称
    private String name;

    // 队伍描述
    private String description;

    // 队伍最大人数
    private Integer maxNum;

    // 队伍状态
    private Integer status;

    @JsonFormat(pattern="yyyy-MM-dd", timezone="GMT+8")
    private Date expireTime;

    // 入队用户列表
    private UserVO user;
}
