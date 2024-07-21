package com.liang.usercenter.service;

import com.liang.usercenter.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

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
    long resister(String userAccount, String passwd, String rePasswd/*, String planetCode*/);

    /**
     * 用户登录
     * @param userAccount 用户账户
     * @param passwd 用户密码
     * @return 脱敏后的用户数据
     */
    User login(String userAccount, String passwd, HttpServletRequest request);

    /**
     * 用户脱敏
     */
    User getSafeUser(User user);

    /**
     * 用户注销
     */
    int logout(HttpServletRequest request);

    List<User> searchUserByTags(List<String> tagNameList);

    /**
     * 更新用户信息
     */
    Integer updateUser(User user, User currentUser);

    /**
     * 获取当前登录用户
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 是否为管理员
     */
    boolean isAdmin(HttpServletRequest request);

    /**
     * 是否为管理员
     */
    boolean isAdmin(User loginUser);
}
