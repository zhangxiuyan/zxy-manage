package xyz.zhangxiuyan.manage.config.xss;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

/**
 * @author zxy
 * @version 1.0 - 2025/11/27
 */
@Configuration
public class XssFilterConfig {

    @Bean
    public FilterRegistrationBean<XssFilter> xssFilterRegistration() {
        FilterRegistrationBean<XssFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new XssFilter(Arrays.asList("/health/**", "/actuator/**")));
        registration.addUrlPatterns("/*");
        registration.setName("XssFilter");
        registration.setOrder(1); // 保证在 Spring Security 之前或者你需要的位置
        return registration;
    }
}
