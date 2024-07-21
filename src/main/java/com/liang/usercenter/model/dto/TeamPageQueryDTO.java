package com.liang.usercenter.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamPageQueryDTO implements Serializable {


    private int page = 1;

    private int pageSize = 10;

    private String name;

    // 队伍最大人数
    private Integer maxNum;

    // 队伍状态
    private Integer status;

    // 用户id
    private Long userId;
}
