package xyz.zhangxiuyan.manage.service;

/**
 * @author zxy
 * @version 1.0 - 2025/10/21
 */
public interface TokenStoreService {

    /**
     * 存储刷新令牌
     *
     * @param rawRefreshToken 原始刷新令牌
     * @param username        用户名
     * @param userId          用户ID
     * @param deviceInfo      设备信息
     * @param ipAddress       IP地址
     * @param ttlSeconds      过期时间(秒)
     */
    void storeRefreshToken(String rawRefreshToken, String username, String userId,
                           String deviceInfo, String ipAddress, long ttlSeconds);

    /**
     * 存储刷新令牌（带token family）
     *
     * @param rawRefreshToken 原始刷新令牌
     * @param username        用户名
     * @param userId          用户ID
     * @param deviceInfo      设备信息
     * @param ipAddress       IP地址
     * @param ttlSeconds      过期时间(秒)
     * @param familyId        token family ID
     */
    void storeRefreshToken(String rawRefreshToken, String username, String userId,
                           String deviceInfo, String ipAddress, long ttlSeconds, String familyId);

    /**
     * 验证刷新令牌是否有效
     *
     * @param rawRefreshToken 原始刷新令牌
     * @return 是否有效
     */
    boolean validateRefreshToken(String rawRefreshToken);

    /**
     * 撤销刷新令牌
     *
     * @param rawRefreshToken 原始刷新令牌
     */
    void revokeRefreshToken(String rawRefreshToken);

    /**
     * 检查Access令牌是否已被加入黑名单
     *
     * @param jti 令牌ID
     * @return 是否在黑名单中
     */
    boolean isAccessTokenBlacklisted(String jti);

    /**
     * 将Access令牌加入黑名单
     *
     * @param jti         令牌ID
     * @param ttlSeconds  剩余有效期(秒)
     */
    void blacklistAccessToken(String jti, long ttlSeconds);

    /**
     * 获取刷新令牌对应的用户信息
     *
     * @param rawRefreshToken 原始刷新令牌
     * @return 用户ID
     */
    String getUserIdByRefreshToken(String rawRefreshToken);

    /**
     * 获取刷新令牌对应的family ID
     *
     * @param rawRefreshToken 原始刷新令牌
     * @return family ID
     */
    String getFamilyIdByRefreshToken(String rawRefreshToken);
}
