package xyz.zhangxiuyan.manage.service;

/**
 * @author zxy
 * @version 1.0 - 2026/04/20
 */
public interface TokenFamilyService {

    /**
     * Create a new token family for a user session
     *
     * @param userId 用户ID
     * @return 新token family ID
     */
    String createTokenFamily(String userId);

    /**
     * Validate if the refresh token is the current valid token in its family
     *
     * @param refreshToken 刷新令牌
     * @param familyId     token family ID
     * @return 是否有效
     */
    boolean isCurrentTokenInFamily(String refreshToken, String familyId);

    /**
     * Advance the token family, invalidating the old token and returning a new family state
     *
     * @param oldRefreshToken 旧刷新令牌
     * @param familyId        token family ID
     * @return 新family state (包含新family ID)
     */
    String advanceTokenFamily(String oldRefreshToken, String familyId);

    /**
     * Check if a token reuse attack is suspected (old token in family being used)
     *
     * @param refreshToken 刷新令牌
     * @param familyId     token family ID
     * @return 是否检测到可能的攻击
     */
    boolean isTokenReuseDetected(String refreshToken, String familyId);

    /**
     * Invalidate all tokens in a token family (e.g., logout from all devices)
     *
     * @param familyId token family ID
     */
    void invalidateFamily(String familyId);
}
