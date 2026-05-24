package xyz.zhangxiuyan.manage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Base32;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import xyz.zhangxiuyan.manage.entity.UserMfa;
import xyz.zhangxiuyan.manage.entity.vo.MfaSetupResponseVO;
import xyz.zhangxiuyan.manage.exception.BusinessException;
import xyz.zhangxiuyan.manage.mapper.UserMfaMapper;
import xyz.zhangxiuyan.manage.service.MfaService;
import xyz.zhangxiuyan.manage.utils.AesUtil;

import javax.annotation.Resource;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/**
 * MFA服务实现
 *
 * @author zxy
 * @version 1.0 - 2025/10/23
 */
@Service
public class MfaServiceImpl extends ServiceImpl<UserMfaMapper, UserMfa> implements MfaService {

    @Value("${security.mfa.totp.issuer:zxy-manage}")
    private String totpIssuer;

    @Value("${security.mfa.totp.code-length:6}")
    private int codeLength;

    @Value("${security.mfa.totp.time-step-seconds:30}")
    private int timeStepSeconds;

    @Value("${security.mfa.backup-codes.count:8}")
    private int backupCodesCount;

    @Value("${security.mfa.backup-codes.length:10}")
    private int backupCodeLength;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private xyz.zhangxiuyan.manage.config.CryptoManager cryptoManager;

private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Base32 base32 = new Base32();

    @Override
    public Object getUserMfa(Long userId) {
        UserMfa mfa = getOne(new LambdaQueryWrapper<UserMfa>().eq(UserMfa::getUserId, userId));
        if (mfa == null) {
            return null;
        }
        // Return a safe representation (without secret key)
        return new MfaInfo(mfa.getUserId(), mfa.getMfaType(), mfa.getEnabled());
    }

    @Override
    public boolean isMfaEnabled(Long userId) {
        UserMfa mfa = getOne(new LambdaQueryWrapper<UserMfa>().eq(UserMfa::getUserId, userId));
        return mfa != null && Boolean.TRUE.equals(mfa.getEnabled());
    }

    @Override
    public MfaSetupResponseVO setupMfa(Long userId, String username) {
        // Generate secret
        byte[] secretBytes = new byte[20];
        new SecureRandom().nextBytes(secretBytes);
        String secret = new String(base32.encode(secretBytes)).replace("=", "");

        // Generate backup codes
        List<String> backupCodes = generateBackupCodes();

        // Encrypt secret for storage
        String encryptedSecret;
        String encryptedBackupCodes;
        try {
            encryptedSecret = AesUtil.encrypt(secret, cryptoManager.getAesKeyBase64());
            encryptedBackupCodes = objectMapper.writeValueAsString(
                    passwordEncoder.encode(backupCodes.get(0)) // Store encoded backup codes
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to encrypt MFA secrets", e);
        }

        // Save to database
        UserMfa existingMfa = getOne(new LambdaQueryWrapper<UserMfa>().eq(UserMfa::getUserId, userId));
        UserMfa mfa = new UserMfa();
        mfa.setUserId(userId);
        mfa.setMfaType("TOTP");
        mfa.setSecretKey(encryptedSecret);
        mfa.setBackupCodes(encryptedBackupCodes);
        mfa.setEnabled(false);
        mfa.setCreatedAt(java.time.LocalDateTime.now());
        mfa.setUpdatedAt(java.time.LocalDateTime.now());

        if (existingMfa != null) {
            mfa.setId(existingMfa.getId());
            updateById(mfa);
        } else {
            save(mfa);
        }

        // Generate QR code URL
        String otpauthUrl = String.format(
                "otpauth://totp/%s:%s?secret=%s&issuer=%s&digits=%d&period=%d",
                totpIssuer,
                username,
                secret,
                totpIssuer,
                codeLength,
                timeStepSeconds
        );

        // Generate QR code as base64 PNG
        String qrCodeBase64 = generateQrCode(otpauthUrl);

        return MfaSetupResponseVO.builder()
                .secretKey(secret)
                .qrCodeBase64(qrCodeBase64)
                .otpauthUrl(otpauthUrl)
                .backupCodes(backupCodes)
                .build();
    }

    @Override
    public boolean enableMfa(Long userId, String mfaCode) {
        UserMfa mfa = getOne(new LambdaQueryWrapper<UserMfa>().eq(UserMfa::getUserId, userId));
        if (mfa == null) {
            throw new BusinessException("business.auth.mfa.not.setup", "MFA not set up");
        }

        // Verify code first
        if (!verifyMfaCode(userId, mfaCode)) {
            return false;
        }

        mfa.setEnabled(true);
        mfa.setUpdatedAt(java.time.LocalDateTime.now());
        return updateById(mfa);
    }

    @Override
    public boolean disableMfa(Long userId, String mfaCode) {
        UserMfa mfa = getOne(new LambdaQueryWrapper<UserMfa>().eq(UserMfa::getUserId, userId));
        if (mfa == null) {
            return false;
        }

        // Verify code first
        if (!verifyMfaCode(userId, mfaCode)) {
            return false;
        }

        mfa.setEnabled(false);
        mfa.setUpdatedAt(java.time.LocalDateTime.now());
        return updateById(mfa);
    }

    @Override
    public boolean verifyMfaCode(Long userId, String mfaCode) {
        UserMfa mfa = getOne(new LambdaQueryWrapper<UserMfa>().eq(UserMfa::getUserId, userId));
        if (mfa == null || !Boolean.TRUE.equals(mfa.getEnabled())) {
            return false;
        }

        String secret;
        try {
            secret = AesUtil.decrypt(mfa.getSecretKey(), cryptoManager.getAesKeyBase64());
        } catch (Exception e) {
            throw new RuntimeException("Failed to decrypt MFA secret", e);
        }

        // TOTP validation with 1 step tolerance (before and after current time)
        for (int offset = -1; offset <= 1; offset++) {
            if (generateTotp(secret, offset).equals(mfaCode)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean verifyBackupCode(Long userId, String backupCode) {
        UserMfa mfa = getOne(new LambdaQueryWrapper<UserMfa>().eq(UserMfa::getUserId, userId));
        if (mfa == null || !Boolean.TRUE.equals(mfa.getEnabled())) {
            return false;
        }

        try {
            List<String> storedCodes = objectMapper.readValue(
                    mfa.getBackupCodes(),
                    new TypeReference<List<String>>() {}
            );

            for (String storedCode : storedCodes) {
                if (passwordEncoder.matches(backupCode, storedCode)) {
                    // Remove used backup code
                    storedCodes.remove(storedCode);
                    mfa.setBackupCodes(objectMapper.writeValueAsString(storedCodes));
                    mfa.setUpdatedAt(java.time.LocalDateTime.now());
                    updateById(mfa);
                    return true;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to verify backup code", e);
        }
        return false;
    }

    /**
     * Generate TOTP code
     */
    private String generateTotp(String secret, int offset) {
        try {
            long time = (System.currentTimeMillis() / 1000 / timeStepSeconds) + offset;
            byte[] timeBytes = ByteBuffer.allocate(8).putLong(time).array();

            Mac mac = Mac.getInstance("HmacSHA1");
            SecretKeySpec key = new SecretKeySpec(base32.decode(secret), "HmacSHA1");
            mac.init(key);
            byte[] hash = mac.doFinal(timeBytes);

            int offsetBits = hash[hash.length - 1] & 0x0F;
            int binary = ((hash[offsetBits] & 0x7F) << 24)
                    | ((hash[offsetBits + 1] & 0xFF) << 16)
                    | ((hash[offsetBits + 2] & 0xFF) << 8)
                    | (hash[offsetBits + 3] & 0xFF);

            int otp = binary % (int) Math.pow(10, codeLength);
            return String.format("%0" + codeLength + "d", otp);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate TOTP", e);
        }
    }

    /**
     * Generate backup codes
     */
    private List<String> generateBackupCodes() {
        List<String> codes = new ArrayList<>();
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < backupCodesCount; i++) {
            String code = String.format("%0" + backupCodeLength + "d", random.nextInt((int) Math.pow(10, backupCodeLength)));
            codes.add(code);
        }
        return codes;
    }

    /**
     * Generate QR code as base64 PNG (simplified - in production use ZXing or similar)
     */
    private String generateQrCode(String otpauthUrl) {
        // In a real implementation, use ZXing or similar library to generate QR code
        // For now, return empty string - frontend can use otpauthUrl directly
        return "";
    }

    /**
     * Safe MFA info for API response
     */
    private static class MfaInfo {
        private final Long userId;
        private final String mfaType;
        private final Boolean enabled;

        public MfaInfo(Long userId, String mfaType, Boolean enabled) {
            this.userId = userId;
            this.mfaType = mfaType;
            this.enabled = enabled;
        }

        public Long getUserId() { return userId; }
        public String getMfaType() { return mfaType; }
        public Boolean getEnabled() { return enabled; }
    }
}
