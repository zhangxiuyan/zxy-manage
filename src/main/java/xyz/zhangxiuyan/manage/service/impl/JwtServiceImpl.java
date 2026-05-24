package xyz.zhangxiuyan.manage.service.impl;

import io.jsonwebtoken.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import xyz.zhangxiuyan.manage.utils.RsaUtil;
import xyz.zhangxiuyan.manage.config.RsaKeyManager;
import xyz.zhangxiuyan.manage.service.JwtService;

import javax.annotation.Resource;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author zxy
 * @version 1.0 - 2025/10/21
 */
@Service
public class JwtServiceImpl implements JwtService {

    private static final String ISSUER = "zxy-manage";
    private static final String AUDIENCE = "zxy-api";

    @Resource
    private RsaKeyManager rsaKeyManager;

    @Value("${jwt.access-token-expiration-seconds:900}")
    private long accessTtlSeconds;

    @Override
    public String generateAccessToken(String username, String userId) {
        return generateAccessToken(username, userId, List.of("ROLE_USER"));
    }

    @Override
    public String generateAccessToken(String username, String userId, List<String> roles) {
        try {
            PrivateKey privateKey = RsaUtil.getPrivateKey(rsaKeyManager.getPrivateKeyBase64());
            long now = System.currentTimeMillis();
            String jti = UUID.randomUUID().toString();  // Unique token ID for blacklist tracking

            return Jwts.builder()
                    .setId(jti)
                    .setSubject(username)
                    .claim("userId", userId)
                    .claim("roles", roles)
                    .claim("iss", ISSUER)
                    .claim("aud", AUDIENCE)
                    .setIssuedAt(new Date(now))
                    .setExpiration(new Date(now + accessTtlSeconds * 1000))
                    .signWith(privateKey, SignatureAlgorithm.RS256)
                    .compact();
        } catch (Exception e) {
            throw new RuntimeException("生成AccessToken失败", e);
        }
    }

    @Override
    public String generateRefreshToken() {
        // UUID + 随机串，长度充足且不可预测
        return UUID.randomUUID().toString().replace("-", "") + RandomStringUtils.randomAlphanumeric(32);
    }

    @Override
    public Jws<Claims> parseAndValidate(String token) {
        try {
            PublicKey publicKey = RsaUtil.getPublicKey(rsaKeyManager.getPublicKeyBase64());
            return Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(token);
        } catch (ExpiredJwtException eje) {
            throw eje;
        } catch (JwtException e) {
            throw new RuntimeException("AccessToken 验证失败", e);
        } catch (Exception e) {
            throw new RuntimeException("解析公钥失败", e);
        }
    }

    @Override
    public String getJti(String token) {
        try {
            Jws<Claims> jws = parseAndValidate(token);
            return jws.getBody().getId();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getUserId(String token) {
        try {
            Jws<Claims> jws = parseAndValidate(token);
            return jws.getBody().get("userId", String.class);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<String> getRoles(String token) {
        try {
            Jws<Claims> jws = parseAndValidate(token);
            return jws.getBody().get("roles", List.class);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public long getExpirationSeconds(String token) {
        try {
            Jws<Claims> jws = parseAndValidate(token);
            Date expiration = jws.getBody().getExpiration();
            long remaining = (expiration.getTime() - System.currentTimeMillis()) / 1000;
            return Math.max(0, remaining);
        } catch (Exception e) {
            return 0;
        }
    }
}
