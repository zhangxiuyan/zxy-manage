package xyz.zhangxiuyan.manage.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("zxy-manage API")
                        .description("zxy-manage 接口文档")
                        .version("v1.0")
                        .contact(new Contact().name("zxy")));
    }
}
