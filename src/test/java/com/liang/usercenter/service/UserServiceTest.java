package com.liang.usercenter.service;

import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.liang.usercenter.model.User;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class UserServiceTest {

    @Resource
    private UserService userService;

    /*@Test
    void resister() {
        String userAccount = "liang";
        String passwd = "";
        String rePasswd = "123456";
        long res = userService.resister(userAccount, passwd, rePasswd);
        Assertions.assertEquals(-1, res);
        userAccount = "12345";
        res = userService.resister(userAccount, passwd, rePasswd);
        Assertions.assertEquals(-1, res);
        userAccount = "li";
        res = userService.resister(userAccount, passwd, rePasswd);
        Assertions.assertEquals(-1, res);
        passwd = "1234567";
        userAccount = "li anfg";
        res = userService.resister(userAccount, passwd, rePasswd);
        Assertions.assertEquals(-1, res);
        passwd = "123456";
        userAccount = "yupi";
        res = userService.resister(userAccount, passwd, rePasswd);
        Assertions.assertTrue(res > 0);
    }*/

    @Test
    public void searchUserByTags() {
        List<String> tagNameList = List.of("java", "c++");
        List<User> userList = userService.searchUserByTags(tagNameList);
        Assertions.assertNotNull(userList);
    }
}