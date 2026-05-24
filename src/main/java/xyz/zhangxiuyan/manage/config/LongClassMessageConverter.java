package xyz.zhangxiuyan.manage.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

import java.math.BigInteger;

/**
 * @author zxy
 * @version 1.0 - 2023/5/17
 */
@Configuration
public class LongClassMessageConverter implements InitializingBean {

    @Resource
    ObjectMapper objectMapper;

    private void registerLongToStringSerializer() {
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(BigInteger.class, ToStringSerializer.instance);
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        // 暂不转换小long，约定前端交互数据时大Long全部转字符串
        // simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
        objectMapper.registerModule(simpleModule);
    }

    @Override
    public void afterPropertiesSet() {
        registerLongToStringSerializer();
    }

}
