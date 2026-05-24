package xyz.zhangxiuyan.manage.service.impl;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import xyz.zhangxiuyan.manage.service.TokenFamilyService;

import jakarta.annotation.Resource;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author zxy
 * @version 1.0 - 2026/04/20
 */
@Service
public class TokenFamilyServiceImpl implements TokenFamilyService {

    private static final String TOKEN_FAMILY_PREFIX = "token_family:";
    private static final String FAMILY_TOKEN_PREFIX = "family_token:";
    private static final long FAMILY_EXPIRATION_SECONDS = 604800 * 2L; // 14 days

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public String createTokenFamily(String userId) {
        String familyId = UUID.randomUUID().toString().replace("-", "");
        String key = TOKEN_FAMILY_PREFIX + familyId;

        stringRedisTemplate.opsForValue().set(key, userId + ":new", FAMILY_EXPIRATION_SECONDS, TimeUnit.SECONDS);

        return familyId;
    }

    @Override
    public boolean isCurrentTokenInFamily(String refreshToken, String familyId) {
        String familyKey = TOKEN_FAMILY_PREFIX + familyId;
        String familyState = stringRedisTemplate.opsForValue().get(familyKey);

        if (familyState == null) {
            return false;
        }

        String tokenKey = FAMILY_TOKEN_PREFIX + familyId + ":" + hashToken(refreshToken);
        return stringRedisTemplate.hasKey(tokenKey);
    }

    @Override
    public String advanceTokenFamily(String oldRefreshToken, String familyId) {
        String familyKey = TOKEN_FAMILY_PREFIX + familyId;
        String familyState = stringRedisTemplate.opsForValue().get(familyKey);

        if (familyState == null) {
            return null;
        }

        // Get userId from family state
        String userId = familyState.split(":")[0];

        // Mark old token as used (not current)
        String oldTokenKey = FAMILY_TOKEN_PREFIX + familyId + ":" + hashToken(oldRefreshToken);
        String oldTokenValue = stringRedisTemplate.opsForValue().get(oldTokenKey);
        if (oldTokenValue != null) {
            // Store as "used" with short TTL for audit purposes
            stringRedisTemplate.opsForValue().set(
                    "used_token:" + oldTokenKey,
                    oldTokenValue,
                    86400,
                    TimeUnit.SECONDS
            );
            stringRedisTemplate.delete(oldTokenKey);
        }

        // Generate new family state
        String newFamilyId = UUID.randomUUID().toString().replace("-", "");
        String newFamilyKey = TOKEN_FAMILY_PREFIX + newFamilyId;
        stringRedisTemplate.opsForValue().set(newFamilyKey, userId + ":new", FAMILY_EXPIRATION_SECONDS, TimeUnit.SECONDS);

        return newFamilyId;
    }

    @Override
    public boolean isTokenReuseDetected(String refreshToken, String familyId) {
        // If the token exists in used_tokens, it means it was already used in this family
        String usedKey = "used_token:" + FAMILY_TOKEN_PREFIX + familyId + ":" + hashToken(refreshToken);
        return stringRedisTemplate.hasKey(usedKey);
    }

    @Override
    public void invalidateFamily(String familyId) {
        String familyKey = TOKEN_FAMILY_PREFIX + familyId;
        String familyState = stringRedisTemplate.opsForValue().getAndDelete(familyKey);

        if (familyState != null) {
            // Delete all tokens in this family
            String pattern = FAMILY_TOKEN_PREFIX + familyId + ":*";
            var keys = stringRedisTemplate.keys(pattern);
            if (keys != null && !keys.isEmpty()) {
                stringRedisTemplate.delete(keys);
            }
        }
    }

    private String hashToken(String token) {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
}
