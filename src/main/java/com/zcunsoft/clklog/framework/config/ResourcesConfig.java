package com.zcunsoft.clklog.framework.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 通用配置
 */
@Configuration
public class ResourcesConfig implements WebMvcConfigurer {

    @Value("${clklog-common.access-control-allow-origin-patterns}")
    private String accessControlAllowOriginPatterns;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
    }

    /**
     * 跨域配置
     * <p>
     * 通配符 "*" 与 allowCredentials=true 不兼容：通配时关闭 credentials；
     * 配置具体域名时可携带凭据。支持英文逗号分隔多个 origin pattern。
     */
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        String patterns = StringUtils.hasText(accessControlAllowOriginPatterns)
                ? accessControlAllowOriginPatterns.trim()
                : "http://localhost:*";

        boolean wildcardOnly = "*".equals(patterns);
        config.setAllowCredentials(!wildcardOnly);

        for (String pattern : patterns.split(",")) {
            String trimmed = pattern.trim();
            if (StringUtils.hasText(trimmed)) {
                config.addAllowedOriginPattern(trimmed);
            }
        }
        if (config.getAllowedOriginPatterns() == null || config.getAllowedOriginPatterns().isEmpty()) {
            config.addAllowedOriginPattern("http://localhost:*");
            config.setAllowCredentials(true);
        }

        config.addAllowedHeader("*");
        config.addAllowedMethod(HttpMethod.GET);
        config.addAllowedMethod(HttpMethod.POST);
        config.addAllowedMethod(HttpMethod.OPTIONS);
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
