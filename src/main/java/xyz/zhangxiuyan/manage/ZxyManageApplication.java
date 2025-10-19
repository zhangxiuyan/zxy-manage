package xyz.zhangxiuyan.manage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

//import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = {"xyz.zhangxiuyan.common", "xyz.zhangxiuyan.manage"})
//@EnableDiscoveryClient
public class ZxyManageApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(ZxyManageApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(ZxyManageApplication.class);
    }

}
