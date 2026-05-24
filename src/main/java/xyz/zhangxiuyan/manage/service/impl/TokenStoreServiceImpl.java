package xyz.zhangxiuyan.manage.service.impl;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import xyz.zhangxiuyan.manage.service.TokenStoreService;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author zxy
 * @version 1.0 - 2025/10/23
 */
@Service
public class TokenStoreServiceImpl implements TokenStoreService {

    private static final String REFRESH_TOKEN_PREFIX = "refresh_token:";
    private static final String ACCESS_TOKEN_BLACKLIST_PREFIX = "access_token_blacklist:";
    private static final String REFRESH_TOKEN_BLACKLIST_PREFIX = "refresh_token_blacklist:";
    private static final String USER_SESSIONS_PREFIX = "user_sessions:";

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void storeRefreshToken(String rawRefreshToken, String username, String userId,
                                  String deviceInfo, String ipAddress, long ttlSeconds) {
        storeRefreshToken(rawRefreshToken, username, userId, deviceInfo, ipAddress, ttlSeconds, null);
    }

    @Override
    public void storeRefreshToken(String rawRefreshToken, String username, String userId,
                                  String deviceInfo, String ipAddress, long ttlSeconds, String familyId) {
        String tokenHash = hashToken(rawRefreshToken);
        String key = REFRESH_TOKEN_PREFIX + tokenHash;

        Map<String, String> data = new HashMap<>();
        data.put("userId", userId);
        data.put("username", username);
        data.put("deviceInfo", deviceInfo != null ? deviceInfo : "");
        data.put("ipAddress", ipAddress != null ? ipAddress : "");
        data.put("issuedAt", String.valueOf(System.currentTimeMillis()));
        data.put("familyId", familyId != null ? familyId : "");

        stringRedisTemplate.opsForHash().putAll(key, data);
        stringRedisTemplate.expire(key, ttlSeconds, TimeUnit.SECONDS);

        // Add to user's session set for concurrent session tracking
        stringRedisTemplate.opsForSet().add(USER_SESSIONS_PREFIX + userId, tokenHash);
    }

    @Override
    public boolean validateRefreshToken(String rawRefreshToken) {
        String tokenHash = hashToken(rawRefreshToken);

        // Check if blacklisted
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(REFRESH_TOKEN_BLACKLIST_PREFIX + tokenHash))) {
            return false;
        }

        // Check if exists
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(REFRESH_TOKEN_PREFIX + tokenHash));
    }

    @Override
    public void revokeRefreshToken(String rawRefreshToken) {
        String tokenHash = hashToken(rawRefreshToken);
        String key = REFRESH_TOKEN_PREFIX + tokenHash;

        // Get userId from token before deleting
        Object userIdObj = stringRedisTemplate.opsForHash().get(key, "userId");
        String userId = userIdObj != null ? userIdObj.toString() : null;

        // Get remaining TTL
        Long ttl = stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);

        // Add to blacklist with same TTL as original
        if (ttl != null && ttl > 0) {
            stringRedisTemplate.opsForValue().set(
                    REFRESH_TOKEN_BLACKLIST_PREFIX + tokenHash,
                    "1",
                    ttl,
                    TimeUnit.SECONDS
            );
        }

        // Remove from Redis
        stringRedisTemplate.delete(key);

        // Remove from user's session set
        if (userId != null) {
            stringRedisTemplate.opsForSet().remove(USER_SESSIONS_PREFIX + userId, tokenHash);
        }
    }

    @Override
    public boolean isAccessTokenBlacklisted(String jti) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(ACCESS_TOKEN_BLACKLIST_PREFIX + jti));
    }

    @Override
    public void blacklistAccessToken(String jti, long ttlSeconds) {
        stringRedisTemplate.opsForValue().set(
                ACCESS_TOKEN_BLACKLIST_PREFIX + jti,
                "1",
                ttlSeconds,
                TimeUnit.SECONDS
        );
    }

    @Override
    public String getUserIdByRefreshToken(String rawRefreshToken) {
        String tokenHash = hashToken(rawRefreshToken);
        String key = REFRESH_TOKEN_PREFIX + tokenHash;
        Object userId = stringRedisTemplate.opsForHash().get(key, "userId");
        return userId != null ? userId.toString() : null;
    }

    @Override
    public String getFamilyIdByRefreshToken(String rawRefreshToken) {
        String tokenHash = hashToken(rawRefreshToken);
        String key = REFRESH_TOKEN_PREFIX + tokenHash;
        Object familyId = stringRedisTemplate.opsForHash().get(key, "familyId");
        String value = familyId != null ? familyId.toString() : null;
        return (value != null && !value.isEmpty()) ? value : null;
    }

    /**
     * SHA-256 hash for token storage (don't store raw tokens)
     */
    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
