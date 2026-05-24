package xyz.zhangxiuyan.manage.utils;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Security;
import java.util.Base64;

/**
 * @author zxy
 * @version 1.0 - 2025/10/21
 */
public class AesUtil {

    private static final String ALGORITHM = "AES";

    private static final String TRANSFORMATION = "AES/ECB/PKCS7Padding";

    static {
        // 注册 BouncyCastle 提供者（只需一次）
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    /**
     * 生成 Base64 编码的 AES Key（256 位）
     * @return
     */
    public static String generateBase64Key() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
            keyGen.init(256);
            SecretKey secretKey = keyGen.generateKey();
            return Base64.getEncoder().encodeToString(secretKey.getEncoded());
        } catch (Exception e) {
            throw new RuntimeException("生成 AES 密钥失败", e);
        }
    }

    /**
     * AES 加密（AES/ECB/PKCS7Padding）
     * @param plain     明文
     * @param base64Key Base64 编码密钥
     * @return          Base64 编码的密文
     */
    public static String encrypt(String plain, String base64Key) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(base64Key);
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION, "BC");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] enc = cipher.doFinal(plain.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(enc);
        } catch (Exception e) {
            throw new RuntimeException("AES 加密失败", e);
        }
    }

    /**
     * AES 解密（AES/ECB/PKCS7Padding）
     * @param base64Cipher  Base64 编码密文
     * @param base64Key     Base64 编码密钥
     * @return              明文
     */
    public static String decrypt(String base64Cipher, String base64Key) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(base64Key);
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION, "BC");
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] dec = cipher.doFinal(Base64.getDecoder().decode(base64Cipher));
            return new String(dec, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("AES 解密失败", e);
        }
    }

}
