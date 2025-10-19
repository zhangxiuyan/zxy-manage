package xyz.zhangxiuyan.manage.common.utils;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * @author zxy
 * @version 1.0 - 2025/10/20
 */
public class RsaUtil {

    /**
     * 默认生成 RSA 密钥对
     * @return
     */
    public static KeyPair generateKeyPairDefault() {
       return generateKeyPair(2048);
    }

    /**
     * 生成 RSA 密钥对
     * @param keySize
     * @return
     */
    public static KeyPair generateKeyPair(int keySize) {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(keySize);
            return generator.generateKeyPair();
        } catch (Exception e) {
            throw new RuntimeException("生成RSA密钥失败", e);
        }
    }

    /**
     * 公钥加密
     * @param plainText
     * @param base64PublicKey
     * @return
     */
    public static String encrypt(String plainText, String base64PublicKey) {
        try {
            PublicKey publicKey = getPublicKey(base64PublicKey);
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("RSA 加密失败", e);
        }
    }

    /**
     * 私钥解密
     * @param base64Cipher
     * @param base64PrivateKey
     * @return
     */
    public static String decrypt(String base64Cipher, String base64PrivateKey) {
        try {
            PrivateKey privateKey = getPrivateKey(base64PrivateKey);
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(base64Cipher));
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("RSA 解密失败", e);
        }
    }

    /**
     * 获取公钥
     * @param base64PublicKey
     * @return
     * @throws Exception
     */
    public static PublicKey getPublicKey(String base64PublicKey) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(base64PublicKey);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        return KeyFactory.getInstance("RSA").generatePublic(spec);
    }

    /**
     * 获取私钥
     * @param base64PrivateKey
     * @return
     * @throws Exception
     */
    public static PrivateKey getPrivateKey(String base64PrivateKey) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(base64PrivateKey);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        return KeyFactory.getInstance("RSA").generatePrivate(spec);
    }

}
