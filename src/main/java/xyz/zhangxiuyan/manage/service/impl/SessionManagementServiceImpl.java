package xyz.zhangxiuyan.manage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import xyz.zhangxiuyan.manage.entity.UserSession;
import xyz.zhangxiuyan.manage.entity.vo.SessionVO;
import xyz.zhangxiuyan.manage.exception.BusinessException;
import xyz.zhangxiuyan.manage.mapper.UserSessionMapper;
import xyz.zhangxiuyan.manage.service.SessionManagementService;

import jakarta.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 会话管理服务实现
 *
 * @author zxy
 * @version 1.0 - 2025/10/23
 */
@Service
public class SessionManagementServiceImpl extends ServiceImpl<UserSessionMapper, UserSession> implements SessionManagementService {

    private static final String USER_SESSIONS_REDIS_PREFIX = "user_sessions:";

    @Value("${security.session.default-max:5}")
    private int defaultMaxConcurrent;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public List<SessionVO> getUserSessions(Long userId) {
        // Get sessions from database
        List<UserSession> sessions = list(new LambdaQueryWrapper<UserSession>()
                .eq(UserSession::getUserId, userId)
                .eq(UserSession::getIsActive, true)
                .orderByDesc(UserSession::getLastAccessedAt));

        // Get current session hashes from Redis
        Set<String> redisSessions = stringRedisTemplate.opsForSet().members(USER_SESSIONS_REDIS_PREFIX + userId);

        List<SessionVO> result = new ArrayList<>();
        for (UserSession session : sessions) {
            boolean isCurrent = redisSessions != null && redisSessions.contains(session.getRefreshTokenHash());
            result.add(SessionVO.builder()
                    .sessionId(session.getId())
                    .deviceInfo(session.getDeviceInfo())
                    .ipAddress(session.getIpAddress())
                    .isCurrent(isCurrent)
                    .createdAt(session.getCreatedAt())
                    .lastAccessedAt(session.getLastAccessedAt())
                    .build());
        }
        return result;
    }

    @Override
    public boolean terminateSession(Long userId, Long sessionId) {
        UserSession session = getOne(new LambdaQueryWrapper<UserSession>()
                .eq(UserSession::getId, sessionId)
                .eq(UserSession::getUserId, userId));

        if (session == null) {
            throw new BusinessException("business.auth.session.not.found", "Session not found");
        }

        // Remove from Redis
        stringRedisTemplate.opsForSet().remove(USER_SESSIONS_REDIS_PREFIX + userId, session.getRefreshTokenHash());

        // Mark as inactive in database
        session.setIsActive(false);
        return updateById(session);
    }

    @Override
    public boolean terminateCurrentSession(Long userId, String refreshToken) {
        if (refreshToken == null) {
            return false;
        }
        String tokenHash = hashToken(refreshToken);

        UserSession session = getOne(new LambdaQueryWrapper<UserSession>()
                .eq(UserSession::getUserId, userId)
                .eq(UserSession::getRefreshTokenHash, tokenHash));

        if (session == null) {
            return false;
        }

        // Remove from Redis
        stringRedisTemplate.opsForSet().remove(USER_SESSIONS_REDIS_PREFIX + userId, tokenHash);

        // Mark as inactive in database
        session.setIsActive(false);
        return updateById(session);
    }

    @Override
    public int terminateAllOtherSessions(Long userId, String excludeTokenHash) {
        List<UserSession> sessions = list(new LambdaQueryWrapper<UserSession>()
                .eq(UserSession::getUserId, userId)
                .eq(UserSession::getIsActive, true));

        int count = 0;
        for (UserSession session : sessions) {
            if (!session.getRefreshTokenHash().equals(excludeTokenHash)) {
                // Remove from Redis
                stringRedisTemplate.opsForSet().remove(USER_SESSIONS_REDIS_PREFIX + userId, session.getRefreshTokenHash());

                // Mark as inactive in database
                session.setIsActive(false);
                updateById(session);
                count++;
            }
        }
        return count;
    }

    @Override
    public boolean isSessionLimitExceeded(Long userId) {
        int maxConcurrent = getMaxConcurrentSessions(userId);
        Set<String> sessions = stringRedisTemplate.opsForSet().members(USER_SESSIONS_REDIS_PREFIX + userId);
        int currentCount = sessions != null ? sessions.size() : 0;
        return currentCount >= maxConcurrent;
    }

    @Override
    public int getMaxConcurrentSessions(Long userId) {
        UserSession session = getOne(new LambdaQueryWrapper<UserSession>()
                .eq(UserSession::getUserId, userId)
                .eq(UserSession::getIsActive, true));

        return session != null && session.getMaxConcurrent() != null
                ? session.getMaxConcurrent()
                : defaultMaxConcurrent;
    }

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
