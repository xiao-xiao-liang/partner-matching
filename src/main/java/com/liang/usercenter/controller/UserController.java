package com.liang.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liang.usercenter.common.BaseResponse;
import com.liang.usercenter.common.ErrorCode;
import com.liang.usercenter.common.ResultUtils;
import com.liang.usercenter.exception.BusinessException;
import com.liang.usercenter.mapper.request.UserLoginRequest;
import com.liang.usercenter.mapper.request.UserRegisterRequest;
import com.liang.usercenter.model.User;
import com.liang.usercenter.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.liang.usercenter.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户接口
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Resource
    private UserService userService;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @PostMapping("/register")
    @Operation(summary = "注册")
    public BaseResponse<Long> register(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String passwd = userRegisterRequest.getPasswd();
        String rePasswd = userRegisterRequest.getRePasswd();
//        String planetCode = userRegisterRequest.getPlanetCode();
        if (StringUtils.isAnyBlank(userAccount, passwd, rePasswd/*, planetCode*/)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        long result = userService.resister(userAccount, passwd, rePasswd/*, planetCode*/);
        return ResultUtils.success(result);
    }

    @PostMapping("/login")
    public BaseResponse<User> login(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "用户不存在");
        }
        String userAccount = userLoginRequest.getUserAccount();
        String passwd = userLoginRequest.getPasswd();
        if (StringUtils.isAnyBlank(userAccount, passwd)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        User user = userService.login(userAccount, passwd, request);
        return ResultUtils.success(user);
    }

    @PostMapping("/logout")
    public BaseResponse<Integer> logout(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        int res = userService.logout(request);
        return ResultUtils.success(res);
    }

    /**
     * 获取当前登录用户
     */
    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "请先登录");
        }
        // TODO 校验用户是否合法
        long id = user.getId();
        User safetyUser = userService.getSafeUser(userService.getById(id));
        return ResultUtils.success(safetyUser);
    }

    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers(String username, HttpServletRequest request) {
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (username != null) {
            queryWrapper.like("username", username);
        }
        List<User> userList = userService.list(queryWrapper);
        List<User> list = userList.stream().peek(user -> userService.getSafeUser(user)).collect(Collectors.toList());
        return ResultUtils.success(list);
    }

    /**
     * 推荐用户
     */
    @GetMapping("/recommend")
    public BaseResponse<Page<User>> recommendUsers(long pageSize, long pageNum, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        String redisKey = String.format("liang:user:recommend:%s", loginUser.getId());
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        // 如果有缓存，直接取
        Page<User> userPage = (Page<User>) valueOperations.get(redisKey);
        if (userPage != null) {
            return ResultUtils.success(userPage);
        }
        // 缓存中不存在，查询数据库
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        userPage = userService.page(new Page<>(pageNum, pageSize), queryWrapper);
        // 查完后，再缓存
        try {
            valueOperations.set(redisKey, userPage, 300000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.error("set Redis key Error", e);
        }
        return ResultUtils.success(userPage);
    }

    @GetMapping("/search/tags")
    public BaseResponse<List<User>> searchUsersByTags(@RequestParam(required = false) List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<User> userList = userService.searchUserByTags(tagNameList);
        return ResultUtils.success(userList);
    }

    @PostMapping("/update")
    public BaseResponse<Integer> updateUser(@RequestBody User user, HttpServletRequest request) {
        // 1.校验参数是否为空
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User currentUser = userService.getLoginUser(request);
        // 3.触发更新
        return ResultUtils.success(userService.updateUser(user, currentUser));
    }

    @DeleteMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody long id, HttpServletRequest request) {
        // 鉴权，仅管理员可查询
        User user = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        if (id < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = userService.removeById(id);
        return ResultUtils.success(b);
    }


}
