package com.liang.usercenter.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@EnableKnife4j
@Profile({"dev", "test"})
public class SwaggerConfig {
    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("伙伴匹配系统")
                        //描叙
                        .description("接口文档")
                        //版本
                        .version("v1")
                        //作者信息，自行设置
                        .contact(new Contact().name("liang"))
                        //设置接口文档的许可证信息
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }
}
