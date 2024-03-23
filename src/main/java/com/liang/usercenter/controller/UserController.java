package com.liang.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.liang.usercenter.mapper.request.UserLoginRequest;
import com.liang.usercenter.mapper.request.UserRegisterRequest;
import com.liang.usercenter.model.User;
import com.liang.usercenter.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.liang.usercenter.constant.UserConstant.ADMIN_ROLE;
import static com.liang.usercenter.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户接口
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/register")
    public Long register(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            return null;
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String passwd = userRegisterRequest.getPasswd();
        String rePasswd = userRegisterRequest.getRePasswd();
        if (StringUtils.isAnyBlank(userAccount, passwd, rePasswd)) {
            return null;
        }
        return userService.resister(userAccount, passwd, rePasswd);
    }

    @GetMapping("/login")
    public User register(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            return null;
        }
        String userAccount = userLoginRequest.getUserAccount();
        String passwd = userLoginRequest.getPasswd();
        if (StringUtils.isAnyBlank(userAccount, passwd)) {
            return null;
        }
        return userService.login(userAccount, passwd, request);
    }

    @GetMapping("/search")
    public List<User> searchUsers(String username, HttpServletRequest request) {
        if (!isAdmin(request)) {
            return new ArrayList<>();
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (username != null) {
            queryWrapper.like("username", username);
        }
        List<User> userList = userService.list();
        return userList.stream().peek(user -> userService.getUser(user)).collect(Collectors.toList());
    }

    @DeleteMapping("/delete")
    public boolean deleteUser(@RequestBody long id, HttpServletRequest request) {
        // 鉴权，仅管理员可查询
        User user = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if (!isAdmin(request)) {
            return false;
        }
        if (id < 0) {
            return false;
        }
        return userService.removeById(id);
    }

    /**
     * 是否为管理员
     * @param request
     * @return
     */
    private boolean isAdmin(HttpServletRequest request) {
        // 鉴权，仅管理员可查询
        User user = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        return user != null && user.getRoles() == ADMIN_ROLE;
    }
}
