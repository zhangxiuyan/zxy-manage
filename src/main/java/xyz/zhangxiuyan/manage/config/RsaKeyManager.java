package xyz.zhangxiuyan.manage.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import xyz.zhangxiuyan.manage.utils.RsaUtil;

import javax.annotation.PostConstruct;
import java.security.KeyPair;
import java.util.Base64;

/**
 * RSA 密钥管理器
 *  - 应用启动时自动生成公私钥对
 *  - 存放在内存中供全局使用
 * @author zxy
 * @version 1.0 - 2025/10/20
 */
@Component
public class RsaKeyManager {

    private static final Logger log = LoggerFactory.getLogger(RsaKeyManager.class);

    private KeyPair keyPair;

    private String publicKeyBase64;

    private String privateKeyBase64;

    public KeyPair getKeyPair() {
        return keyPair;
    }

    public String getPublicKeyBase64() {
        return publicKeyBase64;
    }

    public String getPrivateKeyBase64() {
        return privateKeyBase64;
    }

    @PostConstruct
    public void init() {
        log.info("🔐 正在生成RSA密钥对...");
        keyPair = RsaUtil.generateKeyPairDefault();
        publicKeyBase64 = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
        privateKeyBase64 = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
        log.info("✅ RSA密钥对生成完成，已存入内存");
    }

}
