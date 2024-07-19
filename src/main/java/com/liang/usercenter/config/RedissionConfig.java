package com.liang.usercenter.config;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redissoin 配置
 */
@Configuration
@ConfigurationProperties(prefix = "spring.data.redis")
@Data
public class RedissionConfig {
    private String host;
    private String port;

    // 创建RedissonClient
    @Bean
    public RedissonClient redissonClient() {
        // 1. 创建配置
        Config config = new Config();
        String redisAddress = String.format("redis://%s:%s", host, port);
        config.useSingleServer().setAddress(redisAddress).setDatabase(3);

        // 2. 创建实例
        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }
}
