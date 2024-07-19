package com.liang.usercenter.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    // 跨域问题
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 允许跨域请求的路径
                .allowedOrigins("http://localhost:5173") // 允许的域名列表
                .allowedMethods("GET", "POST", "PUT", "DELETE") // 允许的HTTP方法
                .allowedHeaders("*") // 允许的HTTP头
                .allowCredentials(true); // 是否允许证书（cookies）
//                .maxAge(3600); // 预请求的有效期
    }
}
