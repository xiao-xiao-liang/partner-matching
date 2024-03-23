package com.liang.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liang.usercenter.mapper.UserMapper;
import com.liang.usercenter.model.User;
import com.liang.usercenter.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.regex.Pattern;

import static com.liang.usercenter.constant.UserConstant.USER_LOGIN_STATE;

/**
 * @author 29018
 * @description 针对表【user】的数据库操作Service实现
 * @createDate 2024-03-20 19:09:39
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "liang";

    @Resource
    private UserMapper userMapper;

    @Override
    public long resister(String userAccount, String passwd, String rePasswd) {
        // 1.
        // 检验账户、密码、二次输入密码是否为空
        if (StringUtils.isAnyBlank(userAccount, passwd, rePasswd)) {
            // TODO 修改为自定义异常
            return -1;
        }
        // 账户不小于4位
        if (userAccount.length() < 4) {
            return -1;
        }
        // 密码不小于6位
        if (passwd.length() < 6 || rePasswd.length() < 6) {
            return -1;
        }
        // 账号不包含特殊字符
        String regexPattern = "^[a-zA-Z0-9_\\u4e00-\\u9fa5]+$";
        Pattern pattern = Pattern.compile(regexPattern);
        if (!pattern.matcher(userAccount).matches()) {
            return -1;
        }
        // 检验两次密码是否相同
        if (!passwd.equals(rePasswd)) {
            return -1;
        }
        // 账号不能重复
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("user_account", userAccount);
        if (count(wrapper) > 0) {
            return -1;
        }
        // 2.对密码加密，加盐
        String encryptPasswd = DigestUtils.md5DigestAsHex((SALT + passwd).getBytes());

        // 3.向数据库插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setPasswd(encryptPasswd);
        boolean save = this.save(user);
        if (!save) {
            return -1;
        }
        return user.getId();
    }

    /**
     * 用户登录
     * @param userAccount 用户账户
     * @param passwd 用户密码
     * @return 用户
     */
    @Override
    public User login(String userAccount, String passwd, HttpServletRequest request) {
        // 1.校验
        if (StringUtils.isAnyBlank(userAccount, passwd)) {
            return null;
        }
        if (userAccount.length() < 4) {
            return null;
        }
        if (passwd.length() < 6) {
            return null;
        }
        // 账号不包含特殊字符
        String regexPattern = "^[a-zA-Z0-9_\\u4e00-\\u9fa5]+$";
        Pattern pattern = Pattern.compile(regexPattern);
        if (!pattern.matcher(userAccount).matches()) {
            return null;
        }
        // 2.加密
        String encryptPasswd = DigestUtils.md5DigestAsHex((SALT + passwd).getBytes());
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("user_account", userAccount);
        wrapper.eq("passwd", passwd);
        User user = userMapper.selectOne(wrapper);
        // 用户不存在
        if (user == null) {
            log.info("登录失败，用户名或密码错误");
            return null;
        }
        // 3.用户信息脱敏，
        User safetyUser = getUser(user);
        // 4.记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, safetyUser);
        return safetyUser;
    }

    /**
     * 用户脱敏
     * @param user
     * @return
     */
    @Override
    public User getUser(User user) {
        User safetyUser = new User();
        safetyUser.setId(user.getId());
        safetyUser.setPhone(user.getPhone());
        safetyUser.setGender(user.getGender());
        safetyUser.setUsername(user.getUsername());
        safetyUser.setStatus(user.getStatus());
        safetyUser.setUserAccount(user.getUserAccount());
        safetyUser.setDeleted(user.getDeleted());
        safetyUser.setEmail(user.getEmail());
        safetyUser.setAvatarUrl(user.getAvatarUrl());
        safetyUser.setCreateTime(new Date());
        return safetyUser;
    }
}