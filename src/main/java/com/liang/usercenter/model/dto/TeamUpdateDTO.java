package com.liang.usercenter.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamUpdateDTO {
    // 队伍id
    private Long id;

    // 队伍名称
    private String name;

    // 队伍描述
    private String description;

    // 最大人数
    private Integer maxNum;

    // 0 - 公开，1 - 私有，2 - 加密
    private Integer status;

    private String password;

    @JsonFormat(pattern="yyyy-MM-dd", timezone="GMT+8")
    private Date expireTime;
}
