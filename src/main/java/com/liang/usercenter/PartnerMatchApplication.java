package com.liang.usercenter;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.liang.usercenter.mapper")
// 定时任务
@EnableScheduling
public class PartnerMatchApplication {
	public static void main(String[] args) {
		SpringApplication.run(PartnerMatchApplication.class, args);
	}
}
