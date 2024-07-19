package com.liang.usercenter.once;

import com.liang.usercenter.mapper.UserMapper;
import com.liang.usercenter.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.time.LocalDateTime;
import java.util.Date;

@Component
public class InsertUsers {
    @Autowired
    private UserMapper userMapper;

    /**
     * 批量插入用户
     */
//    @Scheduled(initialDelay = 5000, fixedRate = Long.MAX_VALUE)
    public void doInsertUsers() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        for (int i = 0; i < 10000000; i++) {
            User user = new User();
            user.setUsername("假用户" + i);
            user.setUserAccount("fake" + i);
            user.setAvatarUrl("https://files.codelife.cc/icons/feishu.svg");
            user.setGender(0);
            user.setPasswd("123456");
            user.setPhone("123");
            user.setEmail("123@qq.com");
            user.setStatus(0);
            user.setDeleted(0);
            user.setRoles(0);
            user.setTags("[\"java\",\"python\"]");
            user.setIntroduction("我是一个假用户");
            userMapper.insert(user);
        }
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }

    public static void main(String[] args) {
        new InsertUsers().doInsertUsers();
    }
}
