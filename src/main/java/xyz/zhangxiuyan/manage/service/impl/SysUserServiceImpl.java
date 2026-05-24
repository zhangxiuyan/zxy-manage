package xyz.zhangxiuyan.manage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import xyz.zhangxiuyan.manage.exception.BusinessException;
import xyz.zhangxiuyan.manage.Constants.SystemConstant;
import xyz.zhangxiuyan.manage.entity.LoginUser;
import xyz.zhangxiuyan.manage.entity.SysRole;
import xyz.zhangxiuyan.manage.entity.SysUser;
import xyz.zhangxiuyan.manage.entity.dto.RegisterRequestDTO;
import xyz.zhangxiuyan.manage.entity.vo.UserResponseVO;
import xyz.zhangxiuyan.manage.entity.dto.UserUpdateRequestDTO;
import xyz.zhangxiuyan.manage.entity.vo.SysUserVO;
import xyz.zhangxiuyan.manage.mapper.SysRoleMapper;
import xyz.zhangxiuyan.manage.mapper.SysUserMapper;
import xyz.zhangxiuyan.manage.service.JwtService;
import xyz.zhangxiuyan.manage.service.SysUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zxy
 * @since 2023-06-12
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private JwtService jwtService;

    @Resource
    private SysRoleMapper sysRoleMapper;

    @Override
    public void create(SysUser sysUser) {
        SysUser user = getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getDeleteFlag, SystemConstant.DEFAULT_DELETE_FLAG)
                .eq(SysUser::getEmail, sysUser.getEmail()));
        if (user != null) {
            throw new BusinessException("business.user.email.exists", "Email already exists");
        }
        if (!save(sysUser)) {
            throw new BusinessException("business.user.save.failed", "Save user failed");
        }
    }

    @Override
    public SysUserVO queryUserInfo(HttpServletRequest request) {
        String header = request.getHeader(SystemConstant.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")) {
            throw new BusinessException("business.auth.token.invalid", "Invalid token");
        }

        String token = header.substring(7);
        String userId = jwtService.getUserId(token);
        if (userId == null) {
            throw new BusinessException("business.auth.token.invalid", "Invalid token");
        }

        SysUser user = getById(Long.parseLong(userId));
        if (user == null || user.getDeleteFlag() != 0) {
            throw new BusinessException("business.user.not.found", "User not found");
        }

        SysUserVO vo = new SysUserVO();
        BeanUtils.copyProperties(user, vo);

        // Load role info
        if (user.getRoleId() != null) {
            SysRole role = sysRoleMapper.selectById(user.getRoleId());
            if (role != null && role.getDeleteFlag() == 0 && role.getStatus() == 1) {
                vo.setRole(role.getRoleCode());
            }
        }

        return vo;
    }

    @Override
    public Long register(RegisterRequestDTO request) {
        // Check accountId uniqueness
        if (isAccountIdExists(request.getAccountId())) {
            throw new BusinessException("business.user.accountId.exists", "Account ID already exists");
        }
        // Check nickname uniqueness
        if (isNicknameExists(request.getNickname())) {
            throw new BusinessException("business.user.nickname.exists", "Nickname already exists");
        }

        SysUser user = new SysUser();
        user.setAccountId(request.getAccountId());
        user.setNickname(request.getNickname());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setStatus(1); // enabled
        user.setDeleteFlag(0);
        user.setAvatar("/default-avatar.png");

        if (!save(user)) {
            throw new BusinessException("business.user.registration.failed", "Registration failed");
        }
        return user.getId();
    }

    @Override
    public void updateUserInfo(UserUpdateRequestDTO request, Long userId) {
        SysUser user = getById(userId);
        if (user == null || user.getDeleteFlag() != 0) {
            throw new BusinessException("business.user.not.found", "User not found");
        }

        // Check nickname uniqueness if being updated
        if (request.getNickname() != null && !request.getNickname().equals(user.getNickname())) {
            if (isNicknameExists(request.getNickname())) {
                throw new BusinessException("business.user.nickname.exists", "Nickname already exists");
            }
            user.setNickname(request.getNickname());
        }

        // Check email uniqueness if being updated
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (isEmailExists(request.getEmail())) {
                throw new BusinessException("business.user.email.exists", "Email already exists");
            }
            user.setEmail(request.getEmail());
        }

        // Check mobile uniqueness if being updated
        if (request.getMobile() != null && !request.getMobile().equals(user.getMobile())) {
            if (isMobileExists(request.getMobile())) {
                throw new BusinessException("business.user.mobile.exists", "Mobile already exists");
            }
            user.setMobile(request.getMobile());
        }

        updateById(user);
    }

    @Override
    public UserResponseVO getUserInfoById(Long userId) {
        SysUser user = getById(userId);
        if (user == null || user.getDeleteFlag() != 0) {
            throw new BusinessException("business.user.not.found", "User not found");
        }
        return UserResponseVO.from(user);
    }

    @Override
    public boolean isAccountIdExists(String accountId) {
        return count(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getDeleteFlag, 0)
                .eq(SysUser::getAccountId, accountId)) > 0;
    }

    @Override
    public boolean isNicknameExists(String nickname) {
        return count(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getDeleteFlag, 0)
                .eq(SysUser::getNickname, nickname)) > 0;
    }

    @Override
    public boolean isEmailExists(String email) {
        return count(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getDeleteFlag, 0)
                .eq(SysUser::getEmail, email)) > 0;
    }

    @Override
    public boolean isMobileExists(String mobile) {
        return count(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getDeleteFlag, 0)
                .eq(SysUser::getMobile, mobile)) > 0;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        // Try to find user by accountId, email, or mobile
        SysUser user = getOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getDeleteFlag, 0)
                .and(wrapper -> wrapper
                        .eq(SysUser::getAccountId, username)
                        .or()
                        .eq(SysUser::getEmail, username)
                        .or()
                        .eq(SysUser::getMobile, username)
                ));

        if (user == null) {
            throw new BusinessException("business.auth.user.not.exists", "User does not exist");
        }

        // Load roles
        List<SimpleGrantedAuthority> authorities = loadUserAuthorities(user.getRoleId());

        LoginUser loginUser = new LoginUser();
        loginUser.setId(user.getId());
        loginUser.setUsername(user.getAccountId());
        loginUser.setPassword(user.getPassword());
        loginUser.setNickname(user.getNickname());
        loginUser.setEmail(user.getEmail());
        loginUser.setPhone(user.getMobile());
        loginUser.setEnabled(user.getStatus() != null && user.getStatus() == 1);
        loginUser.setLocked(false); // Lock status should be managed separately
        loginUser.setAuthorities(authorities);

        return loginUser;
    }

    /**
     * Load user authorities (roles) from database
     */
    private List<SimpleGrantedAuthority> loadUserAuthorities(Long roleId) {
        if (roleId == null) {
            return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        }

        SysRole role = sysRoleMapper.selectById(roleId);
        if (role == null || role.getDeleteFlag() != 0 || role.getStatus() != 1) {
            return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        }

        return Collections.singletonList(new SimpleGrantedAuthority(role.getRoleCode()));
    }

}
