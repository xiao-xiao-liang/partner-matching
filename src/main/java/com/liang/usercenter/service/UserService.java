package com.liang.usercenter.service;

import com.liang.usercenter.model.User;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;

/**
* @author 29018
* @description 针对表【user】的数据库操作Service
* @createDate 2024-03-20 19:09:39
*/
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount 用户账户
     * @param passwd      密码
     * @param rePasswd    二次输入密码
     * @return 用户id
     */
    long resister(String userAccount, String passwd, String rePasswd);

    /**
     * 用户登录
     * @param userAccount 用户账户
     * @param passwd 用户密码
     * @return 脱敏后的用户数据
     */
    User login(String userAccount, String passwd, HttpServletRequest request);

    /**
     * 用户脱敏
     * @param user
     * @return
     */
    User getUser(User user);
}
