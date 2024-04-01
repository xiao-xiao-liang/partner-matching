package com.liang.usercenter.mapper.request;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户注册请求体
 */
@Data
public class UserRegisterRequest implements Serializable {

    private String userAccount;
    private String passwd;
    private String rePasswd;
    private String planetCode;

    @Serial
    private static final long serialVersionUID = -2199815645562430502L;
}
