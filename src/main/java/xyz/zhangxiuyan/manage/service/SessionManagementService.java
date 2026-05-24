package xyz.zhangxiuyan.manage.service;

import xyz.zhangxiuyan.manage.entity.vo.SessionVO;

import java.util.List;

/**
 * 会话管理服务接口
 *
 * @author zxy
 * @version 1.0 - 2025/10/23
 */
public interface SessionManagementService {

    /**
     * 获取用户的所有活跃会话
     *
     * @param userId 用户ID
     * @return 会话列表
     */
    List<SessionVO> getUserSessions(Long userId);

    /**
     * 终止指定会话
     *
     * @param userId    用户ID
     * @param sessionId 会话ID
     * @return 是否成功
     */
    boolean terminateSession(Long userId, Long sessionId);

    /**
     * 终止当前会话 (用于登出)
     *
     * @param userId         用户ID
     * @param refreshToken   刷新令牌
     * @return 是否成功
     */
    boolean terminateCurrentSession(Long userId, String refreshToken);

    /**
     * 终止所有其他会话
     *
     * @param userId          用户ID
     * @param excludeTokenHash 排除的令牌哈希
     * @return 终止的会话数量
     */
    int terminateAllOtherSessions(Long userId, String excludeTokenHash);

    /**
     * 检查用户是否超过最大并发会话数
     *
     * @param userId 用户ID
     * @return 是否超过
     */
    boolean isSessionLimitExceeded(Long userId);

    /**
     * 获取最大并发会话数
     *
     * @param userId 用户ID
     * @return 最大并发数
     */
    int getMaxConcurrentSessions(Long userId);
}
