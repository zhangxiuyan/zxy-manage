package xyz.zhangxiuyan.manage.config;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import xyz.zhangxiuyan.manage.utils.AesUtil;

import javax.annotation.PostConstruct;

/**
 * @author zxy
 * @version 1.0 - 2025/10/21
 */
@Component
public class CryptoManager {

    private static final Logger log = LoggerFactory.getLogger(CryptoManager.class);

    @Value("${crypto.aes-key-base64:}")
    private String aesKeyFromConfig;

    private String aesKeyBase64;

    @PostConstruct
    public void init() {
        if (aesKeyFromConfig != null && !StringUtils.isBlank(aesKeyFromConfig)) {
            this.aesKeyBase64 = aesKeyFromConfig;
            log.info("使用配置的 AES key");
        } else {
            this.aesKeyBase64 = AesUtil.generateBase64Key();
            log.info("运行时生成 AES key（保存在内存）");
        }
    }


    public String getAesKeyBase64() {
        return aesKeyBase64;
    }

}
