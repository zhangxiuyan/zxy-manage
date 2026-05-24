package xyz.zhangxiuyan.manage.service;

import xyz.zhangxiuyan.manage.entity.vo.MfaSetupResponseVO;

/**
 * MFA服务接口
 *
 * @author zxy
 * @version 1.0 - 2025/10/23
 */
public interface MfaService {

    /**
     * 获取用户MFA配置
     *
     * @param userId 用户ID
     * @return 用户MFA信息，如果未设置返回null
     */
    Object getUserMfa(Long userId);

    /**
     * 检查用户是否启用了MFA
     *
     * @param userId 用户ID
     * @return 是否启用
     */
    boolean isMfaEnabled(Long userId);

    /**
     * 生成MFA设置信息 (首次设置调用)
     *
     * @param userId   用户ID
     * @param username 用户名 (用于QR码显示)
     * @return MFA设置信息
     */
    MfaSetupResponseVO setupMfa(Long userId, String username);

    /**
     * 启用MFA (验证后调用)
     *
     * @param userId  用户ID
     * @param mfaCode 验证码
     * @return 是否成功
     */
    boolean enableMfa(Long userId, String mfaCode);

    /**
     * 禁用MFA
     *
     * @param userId  用户ID
     * @param mfaCode 验证码
     * @return 是否成功
     */
    boolean disableMfa(Long userId, String mfaCode);

    /**
     * 验证MFA验证码
     *
     * @param userId  用户ID
     * @param mfaCode 验证码
     * @return 是否有效
     */
    boolean verifyMfaCode(Long userId, String mfaCode);

    /**
     * 验证备份码
     *
     * @param userId      用户ID
     * @param backupCode  备份码
     * @return 是否有效
     */
    boolean verifyBackupCode(Long userId, String backupCode);
}
