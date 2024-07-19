package com.liang.usercenter.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liang.usercenter.model.User;
import com.liang.usercenter.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 缓存预热任务
 */
@Component
@Slf4j
public class PreCacheJob {
    @Resource
    private UserService userService;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private RedissonClient redissonClient;

    List<Long> mainUserList = List.of(1L);

    // 每天的23点59分执行
    @Scheduled(cron = "0 0 15 * * ?")
    public void doCacheRecommendUser() {
        RLock rLock = redissonClient.getLock("liang:precachejob:docache:lock");
        try {
            // 只有一个线程能获取到锁
            if (rLock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                System.out.println("get lock" + Thread.currentThread().getId());
                for (Long userId : mainUserList) {
                    String redisKey = String.format("liang:user:recommend:%s", userId);
                    ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
                    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                    Page<User> userPage = userService.page(new Page<>(1, 20), queryWrapper);
                    try {
                        valueOperations.set(redisKey, userPage, 300000, TimeUnit.MILLISECONDS);
                    } catch (Exception e) {
                        log.error("set Redis key Error", e);
                    }
                }
                // 一定要放到finally中，否则可能会有异常情况导致锁无法释放
                /*if (rLock.isHeldByCurrentThread()) {
                    rLock.unlock();
                }*/
            }
        } catch (InterruptedException e) {
            log.error("doCacheRecommendUser error", e);
        } finally {
            // 只能释放自己的锁
            if (rLock.isHeldByCurrentThread()) {
                System.out.println("unlock" + Thread.currentThread().getId());
                rLock.unlock();
            }
        }
    }
}
