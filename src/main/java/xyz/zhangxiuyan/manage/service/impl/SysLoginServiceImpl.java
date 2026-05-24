package xyz.zhangxiuyan.manage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import xyz.zhangxiuyan.manage.exception.BusinessException;
import xyz.zhangxiuyan.manage.Constants.SystemConstant;
import xyz.zhangxiuyan.manage.Enums.SystemParameterEnum;
import xyz.zhangxiuyan.manage.entity.SysParameter;
import xyz.zhangxiuyan.manage.entity.SysRole;
import xyz.zhangxiuyan.manage.entity.SysUser;
import xyz.zhangxiuyan.manage.entity.dto.LoginRequestDTO;
import xyz.zhangxiuyan.manage.entity.vo.LoginResponseVO;
import xyz.zhangxiuyan.manage.mapper.SysRoleMapper;
import xyz.zhangxiuyan.manage.service.AttackPreventionService;
import xyz.zhangxiuyan.manage.service.JwtService;
import xyz.zhangxiuyan.manage.service.LoginLogService;
import xyz.zhangxiuyan.manage.service.MfaService;
import xyz.zhangxiuyan.manage.service.SessionManagementService;
import xyz.zhangxiuyan.manage.service.SysLoginService;
import xyz.zhangxiuyan.manage.service.SysParameterService;
import xyz.zhangxiuyan.manage.service.SysUserService;
import xyz.zhangxiuyan.manage.service.TokenFamilyService;
import xyz.zhangxiuyan.manage.service.TokenStoreService;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author zxy
 * @version 1.0 - 2023/9/1
 */
@Service
public class SysLoginServiceImpl implements SysLoginService {

    private static final Logger log = LoggerFactory.getLogger(SysLoginServiceImpl.class);

    @Resource
    private SysUserService sysUserService;

    @Resource
    private SysParameterService sysParameterService;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private JwtService jwtService;

    @Resource
    private TokenStoreService tokenStoreService;

    @Resource
    private TokenFamilyService tokenFamilyService;

    @Resource
    private AttackPreventionService attackPreventionService;

    @Resource
    private LoginLogService loginLogService;

    @Resource
    private MfaService mfaService;

    @Resource
    private SessionManagementService sessionManagementService;

    @Resource
    private SysRoleMapper sysRoleMapper;

    @Value("${jwt.access-token-expiration-seconds:900}")
    private long accessTokenExpirationSeconds;

    @Value("${jwt.refresh-token-expiration-seconds:604800}")
    private long refreshTokenExpirationSeconds;

    @Override
    public LoginResponseVO userLogin(LoginRequestDTO loginRequest, HttpServletRequest request, HttpServletResponse response) {
        String ipAddress = getClientIpAddress(request);
        String deviceInfo = request.getHeader("User-Agent");
        String principal = loginRequest.getPrincipal();

        // Determine login type by checking which field was provided
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getDeleteFlag, 0);

        if (principal.contains("@")) {
            // Email login
            queryWrapper.eq(SysUser::getEmail, principal);
        } else if (principal.matches("^[0-9]+$")) {
            // Mobile login
            queryWrapper.eq(SysUser::getMobile, principal);
        } else {
            // AccountId login
            queryWrapper.eq(SysUser::getAccountId, principal);
        }

        SysUser user = sysUserService.getOne(queryWrapper);
        if (user == null) {
            throw new BusinessException("business.auth.user.not.exists", "User does not exist");
        }

        // Check attack prevention - check if account is locked or IP is rate limited
        attackPreventionService.checkLoginAttempt(user.getId().toString(), ipAddress);

        // Check account status - disabled accounts cannot login
        if (user.getStatus() == 0) {
            throw new BusinessException("business.user.account.disabled", "Account is disabled");
        }

        // Verify password with BCrypt
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            // Record failed attempt (includes lockout logging if threshold reached)
            attackPreventionService.recordFailedAttempt(user.getId().toString(), user.getAccountId(), ipAddress);
            // Log failed login
            loginLogService.logFailed(user.getId(), user.getAccountId(), "PASSWORD",
                    ipAddress, deviceInfo, request.getHeader("User-Agent"), "Wrong password");
            throw new BusinessException("business.auth.wrong.password", "Wrong password");
        }

        // Check if MFA is enabled for this user
        boolean mfaEnabled = mfaService.isMfaEnabled(user.getId());

        // Record successful login - clear failed attempt counters
        attackPreventionService.recordSuccessfulLogin(user.getId().toString());

        // Log successful login
        loginLogService.logSuccess(user.getId(), user.getAccountId(), "PASSWORD",
                ipAddress, deviceInfo, request.getHeader("User-Agent"));

        // Load user roles for token
        List<String> roles = loadUserRoles(user.getRoleId());

        // Generate tokens with roles
        String accessToken = jwtService.generateAccessToken(user.getAccountId(), user.getId().toString(), roles);
        String refreshToken = jwtService.generateRefreshToken();

        // Create token family for this session
        String familyId = tokenFamilyService.createTokenFamily(user.getId().toString());

        // Store refresh token in Redis with family ID
        tokenStoreService.storeRefreshToken(
                refreshToken,
                user.getAccountId(),
                user.getId().toString(),
                deviceInfo,
                ipAddress,
                refreshTokenExpirationSeconds,
                familyId
        );

        // Update last login info
        user.setLastLoginTime(LocalDateTime.now());
        user.setLastLoginIp(ipAddress);
        sysUserService.updateById(user);

        return LoginResponseVO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(accessTokenExpirationSeconds)
                .mfaRequired(mfaEnabled)
                .build();
    }

    @Override
    public LoginResponseVO refreshToken(String refreshToken, HttpServletRequest request, HttpServletResponse response) {
        // Validate refresh token
        if (!tokenStoreService.validateRefreshToken(refreshToken)) {
            throw new BusinessException("business.auth.refresh.token.invalid", "Invalid or expired refresh token");
        }

        // Get user ID from refresh token
        String userId = tokenStoreService.getUserIdByRefreshToken(refreshToken);
        if (userId == null) {
            throw new BusinessException("business.auth.refresh.token.invalid", "Invalid refresh token");
        }

        // Get family ID
        String familyId = tokenStoreService.getFamilyIdByRefreshToken(refreshToken);

        // Check for token reuse attack
        if (familyId != null && tokenFamilyService.isTokenReuseDetected(refreshToken, familyId)) {
            log.warn("Potential token reuse attack detected for userId: {}, familyId: {}", userId, familyId);
            // Invalidate the entire family to be safe
            tokenFamilyService.invalidateFamily(familyId);
            throw new BusinessException("security.token.reuse.detected", "Security violation detected");
        }

        // Load user to get accountId
        SysUser user = sysUserService.getById(Long.parseLong(userId));
        if (user == null || user.getDeleteFlag() != 0) {
            throw new BusinessException("business.auth.user.not.exists", "User does not exist");
        }

        if (user.getStatus() == 0) {
            throw new BusinessException("business.user.account.disabled", "Account is disabled");
        }

        // Load user roles
        List<String> roles = loadUserRoles(user.getRoleId());

        // Advance token family (rotates the token)
        String newFamilyId = familyId != null
                ? tokenFamilyService.advanceTokenFamily(refreshToken, familyId)
                : tokenFamilyService.createTokenFamily(user.getId().toString());

        // Revoke old refresh token
        tokenStoreService.revokeRefreshToken(refreshToken);

        // Generate new tokens with roles
        String newAccessToken = jwtService.generateAccessToken(user.getAccountId(), user.getId().toString(), roles);
        String newRefreshToken = jwtService.generateRefreshToken();

        // Store new refresh token
        String ipAddress = getClientIpAddress(request);
        String deviceInfo = request.getHeader("User-Agent");
        tokenStoreService.storeRefreshToken(
                newRefreshToken,
                user.getAccountId(),
                user.getId().toString(),
                deviceInfo,
                ipAddress,
                refreshTokenExpirationSeconds,
                newFamilyId
        );

        return LoginResponseVO.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(accessTokenExpirationSeconds)
                .mfaRequired(false)
                .build();
    }

    @Override
    public void logout(String accessToken, String refreshToken) {
        // Blacklist access token if provided
        if (accessToken != null) {
            String jti = jwtService.getJti(accessToken);
            if (jti != null) {
                long ttl = jwtService.getExpirationSeconds(accessToken);
                if (ttl > 0) {
                    tokenStoreService.blacklistAccessToken(jti, ttl);
                }
            }
        }

        // Revoke refresh token and invalidate family if provided
        if (refreshToken != null) {
            String familyId = tokenStoreService.getFamilyIdByRefreshToken(refreshToken);
            if (familyId != null) {
                tokenFamilyService.invalidateFamily(familyId);
            }
            tokenStoreService.revokeRefreshToken(refreshToken);
        }
    }

    @Override
    public String secondStepCode() {
        SysParameter parameter = sysParameterService.getOne(new LambdaQueryWrapper<SysParameter>()
                .eq(SysParameter::getParameterDeleteMark, SystemConstant.DEFAULT_DELETE_FLAG)
                .eq(SysParameter::getParameterKey, SystemConstant.SECOND_STEP_CODE));
        if (parameter == null) {
            return SystemParameterEnum.OFF.getValue();
        }
        return parameter.getParameterValue();
    }

    /**
     * Load user roles from database
     */
    private List<String> loadUserRoles(Long roleId) {
        if (roleId == null) {
            return java.util.Collections.singletonList("ROLE_USER");
        }

        SysRole role = sysRoleMapper.selectById(roleId);
        if (role == null || role.getDeleteFlag() != 0 || role.getStatus() != 1) {
            return java.util.Collections.singletonList("ROLE_USER");
        }

        return java.util.Collections.singletonList(role.getRoleCode());
    }

    /**
     * 获取客户端IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // If multiple IPs, take the first one
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
