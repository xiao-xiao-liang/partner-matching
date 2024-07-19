package com.liang.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.liang.usercenter.common.ErrorCode;
import com.liang.usercenter.constant.UserConstant;
import com.liang.usercenter.exception.BusinessException;
import com.liang.usercenter.mapper.UserMapper;
import com.liang.usercenter.model.User;
import com.liang.usercenter.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
    public long resister(String userAccount, String passwd, String rePasswd/*, String planetCode*/) {
        // 1.
        // 检验账户、密码、二次输入密码是否为空
        if (StringUtils.isAnyBlank(userAccount, passwd, rePasswd)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        // 账户不小于4位
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号过短");
        }
        // 密码不小于6位
        if (passwd.length() < 6 || rePasswd.length() < 6) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码过短");
        }
        // 账号不包含特殊字符
        String regexPattern = "^[a-zA-Z0-9_\\u4e00-\\u9fa5]+$";
        Pattern pattern = Pattern.compile(regexPattern);
        if (!pattern.matcher(userAccount).matches()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号包含特殊字符");
        }
        // 检验两次密码是否相同
        if (!passwd.equals(rePasswd)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 账号不能重复
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("user_account", userAccount);
        long count = userMapper.selectCount(wrapper);
        if (count(wrapper) > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
        }

        // 2.对密码加密，加盐
        String encryptPasswd = DigestUtils.md5DigestAsHex((SALT + passwd).getBytes());

        // 3.向数据库插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setPasswd(encryptPasswd);
//        user.setPlanetCode(planetCode);
        boolean save = this.save(user);
        if (!save) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "插入用户失败");
        }
        return user.getId();
    }


    /**
     * 用户登录
     *
     * @param userAccount 用户账户
     * @param passwd      用户密码
     * @return 用户
     */
    @Override
    public User login(String userAccount, String passwd, HttpServletRequest request) {
        // 1.校验
        if (StringUtils.isAnyBlank(userAccount, passwd)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号过短");
        }
        if (passwd.length() < 6) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码过短");
        }
        // 账号不包含特殊字符
        String regexPattern = "^[a-zA-Z0-9_\\u4e00-\\u9fa5]+$";
        Pattern pattern = Pattern.compile(regexPattern);
        if (!pattern.matcher(userAccount).matches()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号包含特殊字符");
        }
        // 2.加密
        String encryptPasswd = DigestUtils.md5DigestAsHex((SALT + passwd).getBytes());
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("user_account", userAccount);
        wrapper.eq("passwd", encryptPasswd);
        User user = userMapper.selectOne(wrapper);
        // 用户不存在
        if (user == null) {
            log.info("登录失败，用户名或密码错误");
            throw new BusinessException(ErrorCode.NULL_ERROR, "用户不存在");
        }
        // 3.用户信息脱敏，
        User safetyUser = getSafeUser(user);
        // 4.记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, safetyUser);
        return safetyUser;
    }

    /**
     * 用户脱敏
     *
     * @param user
     * @return
     */
    @Override
    public User getSafeUser(User user) {
        if (user == null) {
            return null;
        }
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
        safetyUser.setRoles(user.getRoles());
        safetyUser.setTags(user.getTags());
        safetyUser.setIntroduction(user.getIntroduction());
        return safetyUser;
    }

    @Override
    public int logout(HttpServletRequest request) {
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }

    /**
     * 根据标签搜素用户（内存查询）
     * @param tagNameList 标签列表
     * @return
     */
    @Override
    public List<User> searchUserByTags(List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 1.先查询所有用户
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        List<User> userList = userMapper.selectList(wrapper);
        // 2.在内存中判断是否包含要求的标签
        Gson gson = new Gson();
        return userList.stream().filter(user -> {
            String tags = user.getTags();
            Set<String> tempTagNameSet = gson.fromJson(tags, new TypeToken<Set<String>>() {}.getType());
            tempTagNameSet = Optional.ofNullable(tempTagNameSet).orElse(new HashSet<>());
            /*if (StringUtils.isBlank(tags)) {
                return false;
            }*/
            for (String tagName : tagNameList) {
                if (!tempTagNameSet.contains(tagName)) {
                    return false;
                }
            }
            return true;
        }).map(this::getSafeUser).collect(Collectors.toList());
    }

    @Override
    public Integer updateUser(User user, User loginUser) {
        Long id = user.getId();
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // TODO 如果用户没有传递任何要更新的值，不需要执行update语句，直接报错
        // 1.如果是管理员，可以更新任意用户
        // 2.非管理员，只允许更新自己的信息
        if (!isAdmin(loginUser) && !id.equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        User oldUser = userMapper.selectById(id);
        if (oldUser == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        return userMapper.updateById(user);
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        User loginUser = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        return loginUser;
    }

    @Override
    public boolean isAdmin(HttpServletRequest request) {
        // 鉴权，仅管理员可查询
        User user = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        return user != null && user.getRoles() == UserConstant.ADMIN_ROLE;
    }

    @Override
    public boolean isAdmin(User loginUser) {
        return loginUser != null && loginUser.getRoles() == UserConstant.ADMIN_ROLE;
    }

    /**
     * 根据标签搜素用户（SQL查询）
     * @param tagNameList 标签列表
     * @return
     */
    @Deprecated
    private List<User> searchUserByTagsBySQL(List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        // 拼接 and 查询，必须匹配传入的所有标签
        for (String tag : tagNameList) {
            wrapper = wrapper.like("tags", tag);
        }
        List<User> userList = userMapper.selectList(wrapper);
        return userList.stream().map(this::getSafeUser).collect(Collectors.toList());
    }
}