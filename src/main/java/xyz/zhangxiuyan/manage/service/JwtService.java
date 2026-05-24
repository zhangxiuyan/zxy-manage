package xyz.zhangxiuyan.manage.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

import java.util.List;

/**
 * @author zxy
 * @version 1.0 - 2025/10/21
 */
public interface JwtService {

    /**
     * 生成 access token
     *
     * @param username 用户名
     * @param userId   用户ID
     * @return JWT token
     */
    String generateAccessToken(String username, String userId);

    /**
     * 生成 access token with roles
     *
     * @param username 用户名
     * @param userId   用户ID
     * @param roles    用户角色列表
     * @return JWT token
     */
    String generateAccessToken(String username, String userId, List<String> roles);

    /**
     * 生成 refresh token
     *
     * @return refresh token
     */
    String generateRefreshToken();

    /**
     * 解析 token
     *
     * @param token JWT token
     * @return 解析后的 Claims
     */
    Jws<Claims> parseAndValidate(String token);

    /**
     * 获取 token 中的 jti (JWT ID)
     *
     * @param token JWT token
     * @return jti
     */
    String getJti(String token);

    /**
     * 获取 token 中的 userId
     *
     * @param token JWT token
     * @return userId
     */
    String getUserId(String token);

    /**
     * 获取 token 中的 roles
     *
     * @param token JWT token
     * @return roles列表
     */
    List<String> getRoles(String token);

    /**
     * 获取 token 剩余有效期(秒)
     *
     * @param token JWT token
     * @return 剩余秒数
     */
    long getExpirationSeconds(String token);
}
