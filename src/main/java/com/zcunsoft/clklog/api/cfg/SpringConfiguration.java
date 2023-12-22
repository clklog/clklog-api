package com.zcunsoft.clklog.api.cfg;

import com.zcunsoft.clklog.api.handlers.ConstsDataHolder;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableConfigurationProperties({ ClklogApiSetting.class })
public class SpringConfiguration {

    @Bean
    public ConstsDataHolder constsDataHolder() {
        return new ConstsDataHolder();
    }
    
    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                // 接口文档标题
                .info(new Info().title("ClkLog API")
                        // 接口文档简介
                        .description("ClkLog API")
                        // 接口文档版本
                        .version("v1.0")
                        // 开发者联系方式
                        .contact(new Contact().name("ClkLog")));
//                .externalDocs(new ExternalDocumentation()
//                        .description("SpringBoot基础框架")
//                        .url("http://127.0.0.1:8088"));
    }
}
