package xyz.zhangxiuyan.manage.service.impl;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import xyz.zhangxiuyan.common.exception.BusinessException;
import xyz.zhangxiuyan.common.utils.AESUtil;
import xyz.zhangxiuyan.common.utils.JWTUtil;
import xyz.zhangxiuyan.manage.common.Constants.SystemConstant;
import xyz.zhangxiuyan.manage.entity.SysUser;
import xyz.zhangxiuyan.manage.entity.vo.SysUserVO;
import xyz.zhangxiuyan.manage.mapper.SysUserMapper;
import xyz.zhangxiuyan.manage.service.SysUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.security.GeneralSecurityException;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zxy
 * @since 2023-06-12
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService, UserDetailsService {

    @Override
    public void create(SysUser sysUser) {
        SysUser user = getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getDeleteFlag, SystemConstant.DEFAULT_DELETE_FLAG)
                .eq(SysUser::getEmail, sysUser.getEmail()));
        if (user != null) {
            throw new BusinessException("Account already occupied");
        }
        if (!save(sysUser)) {
            throw new BusinessException("Save user failed");
        }
    }

    @Override
    public SysUserVO queryUserInfo(HttpServletRequest request) {
        String token = request.getHeader(SystemConstant.ACCESS_TOKEN);
        String decrypt;
        try {
            decrypt = AESUtil.decrypt(token);
        } catch (GeneralSecurityException e) {
            log.error(e.getMessage(), e);
            throw new BusinessException("Invalid token");
        }
        DecodedJWT tokenInfo = JWTUtil.getTokenInfo(decrypt);
        String idStr = tokenInfo.getClaim("id").asString();
        Long id = Long.parseLong(idStr);
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getDeleteFlag, 0)
                .eq(SysUser::getId, id);
        SysUser user = getOne(queryWrapper);
        if (user == null) {
            throw new BusinessException("user is not exists");
        }
        SysUserVO vo = new SysUserVO();
        BeanUtils.copyProperties(user, vo);
        vo.setRole("{\"id\":\"admin\",\"name\":\"管理员\",\"describe\":\"拥有所有权限\",\"status\":1,\"creatorId\":\"system\",\"createTime\":1497160610259,\"deleted\":0,\"permissions\":[{\"roleId\":\"admin\",\"permissionId\":\"support\",\"permissionName\":\"超级模块\",\"actions\":\"[{\\\"action\\\":\\\"add\\\",\\\"defaultCheck\\\":false,\\\"describe\\\":\\\"新增\\\"},{\\\"action\\\":\\\"import\\\",\\\"defaultCheck\\\":false,\\\"describe\\\":\\\"导入\\\"},{\\\"action\\\":\\\"get\\\",\\\"defaultCheck\\\":false,\\\"describe\\\":\\\"详情\\\"},{\\\"action\\\":\\\"update\\\",\\\"defaultCheck\\\":false,\\\"describe\\\":\\\"修改\\\"},{\\\"action\\\":\\\"delete\\\",\\\"defaultCheck\\\":false,\\\"describe\\\":\\\"删除\\\"},{\\\"action\\\":\\\"export\\\",\\\"defaultCheck\\\":false,\\\"describe\\\":\\\"导出\\\"}]\",\"actionEntitySet\":[{\"action\":\"add\",\"describe\":\"新增\",\"defaultCheck\":false},{\"action\":\"import\",\"describe\":\"导入\",\"defaultCheck\":false},{\"action\":\"get\",\"describe\":\"详情\",\"defaultCheck\":false},{\"action\":\"update\",\"describe\":\"修改\",\"defaultCheck\":false},{\"action\":\"delete\",\"describe\":\"删除\",\"defaultCheck\":false},{\"action\":\"export\",\"describe\":\"导出\",\"defaultCheck\":false}],\"actionList\":null,\"dataAccess\":null},{\"roleId\":\"admin\",\"permissionId\":\"dashboard\",\"permissionName\":\"仪表盘\",\"actions\":\"[{\\\"action\\\":\\\"add\\\",\\\"defaultCheck\\\":false,\\\"describe\\\":\\\"新增\\\"},{\\\"action\\\":\\\"query\\\",\\\"defaultCheck\\\":false,\\\"describe\\\":\\\"查询\\\"},{\\\"action\\\":\\\"get\\\",\\\"defaultCheck\\\":false,\\\"describe\\\":\\\"详情\\\"},{\\\"action\\\":\\\"update\\\",\\\"defaultCheck\\\":false,\\\"describe\\\":\\\"修改\\\"},{\\\"action\\\":\\\"delete\\\",\\\"defaultCheck\\\":false,\\\"describe\\\":\\\"删除\\\"}]\",\"actionEntitySet\":[{\"action\":\"add\",\"describe\":\"新增\",\"defaultCheck\":false},{\"action\":\"query\",\"describe\":\"查询\",\"defaultCheck\":false},{\"action\":\"get\",\"describe\":\"详情\",\"defaultCheck\":false},{\"action\":\"update\",\"describe\":\"修改\",\"defaultCheck\":false},{\"action\":\"delete\",\"describe\":\"删除\",\"defaultCheck\":false}],\"actionList\":null,\"dataAccess\":null},{\"roleId\":\"admin\",\"permissionId\":\"exception\",\"permissionName\":\"异常页面权限\",\"actions\":\"[{\\\"action\\\":\\\"add\\\",\\\"defaultCheck\\\":false,\\\"describe\\\":\\\"新增\\\"},{\\\"action\\\":\\\"query\\\",\\\"defaultCheck\\\":false,\\\"describe\\\":\\\"查询\\\"},{\\\"action\\\":\\\"get\\\",\\\"defaultCheck\\\":false,\\\"describe\\\":\\\"详情\\\"},{\\\"action\\\":\\\"update\\\",\\\"defaultCheck\\\":false,\\\"describe\\\":\\\"修改\\\"},{\\\"action\\\":\\\"delete\\\",\\\"defaultCheck\\\":false,\\\"describe\\\":\\\"删除\\\"}]\",\"actionEntitySet\":[{\"action\":\"add\",\"describe\":\"新增\",\"defaultCheck\":false},{\"action\":\"query\",\"describe\":\"查询\",\"defaultCheck\":false},{\"action\":\"get\",\"describe\":\"详情\",\"defaultCheck\":false},{\"action\":\"update\",\"describe\":\"修改\",\"defaultCheck\":false},{\"action\":\"delete\",\"describe\":\"删除\",\"defaultCheck\":false}],\"actionList\":null,\"dataAccess\":null},{\"roleId\":\"admin\",\"permissionId\":\"result\",\"permissionName\":\"结果权限\",\"actions\":\"[{\\\"action\\\":\\\"add\\\",\\\"defaultCheck\\\":false,\\\"describe\\\":\\\"新增\\\"},{\\\"action\\\":\\\"query\\\",\\\"defaultCheck\\\":false,\\\"describe\\\":\\\"查询\\\"},{\\\"action\\\":\\\"get\\\",\\\"defaultCheck\\\":false,\\\"describe\\\":\\\"详情\\\"},{\\\"action\\\":\\\"update\\\",\\\"defaultCheck\\\":false,\\\"describe\\\":\\\"修改\\\"},{\\\"action\\\":\\\"delete\\\",\\\"defaultCheck\\\":false,\\\"describe\\\":\\\"删除\\\"}]\",\"actionEntitySet\":[{\"action\":\"add\",\"describe\":\"新增\",\"defaultCheck\":false},{\"action\":\"query\",\"describe\":\"查询\",\"defaultCheck\":false},{\"action\":\"get\",\"describe\":\"详情\",\"defaultCheck\":false},{\"action\":\"update\",\"describe\":\"修改\",\"defaultCheck\":false},{\"action\":\"delete\",\"describe\":\"删除\",\"defaultCheck\":false}],\"actionList\":null,\"dataAccess\":null},{\"roleId\":\"admin\",\"permissionId\":\"profile\",\"permissionName\":\"详细页权限\",\"actions\":\"[{\\\"action\\\":\\\"add\\\",\\\"defaultCheck\\\":false,\\\"describe\\\":\\\"新增\\\"},{\\\"action\\\":\\\"query\\\",\\\"defaultCheck\\\":false,\\\"describe\\\":\\\"查询\\\"},{\\\"action\\\":\\\"get\\\",\\\"defaultCheck\\\":false,\\\"describe\\\":\\\"详情\\\"},{\\\"action\\\":\\\"update\\\",\\\"defaultCheck\\\":false,\\\"describe\\\":\\\"修改\\\"},{\\\"action\\\":\\\"delete\\\",\\\"defaultCheck\\\":false,\\\"describe\\\":\\\"删除\\\"}]\",\"actionEntitySet\":[{\"action\":\"add\",\"describe\":\"新增\",\"defaultCheck\":false},{\"action\":\"query\",\"describe\":\"查询\",\"defaultCheck\":false},{\"action\":\"get\",\"describe\":\"详情\",\"defaultCheck\":false},{\"action\":\"update\",\"describe\":\"修改\",\"defaultCheck\":false},{\"action\":\"delete\",\"describe\":\"删除\",\"defaultCheck\":false}],\"actionList\":null,\"dataAccess\":null},{\"roleId\":\"admin\",\"permissionId\":\"table\",\"permissionName\":\"表格权限\",\"actions\":\"[{\\\"action\\\":\\\"add\\\",\\\"defaultCheck\\\":false,\\\"describe\\\":\\\"新增\\\"},{\\\"action\\\":\\\"import\\\",\\\"defaultCheck\\\":false,\\\"describe\\\":\\\"导入\\\"},{\\\"action\\\":\\\"get\\\",\\\"defaultCheck\\\":false,\\\"describe\\\":\\\"详情\\\"},{\\\"action\\\":\\\"update\\\",\\\"defaultCheck\\\":false,\\\"describe\\\":\\\"修改\\\"}]\",\"actionEntitySet\":[{\"action\":\"add\",\"describe\":\"新增\",\"defaultCheck\":false},{\"action\":\"import\",\"describe\":\"导入\",\"defaultCheck\":false},{\"action\":\"get\",\"describe\":\"详情\",\"defaultCheck\":false},{\"action\":\"update\",\"describe\":\"修改\",\"defaultCheck\":false}],\"actionList\":null,\"dataAccess\":null},{\"roleId\":\"admin\",\"permissionId\":\"form\",\"permissionName\":\"表单权限\",\"actions\":\"[{\\\"action\\\":\\\"add\\\",\\\"defaultCheck\\\":false,\\\"describe\\\":\\\"新增\\\"},{\\\"action\\\":\\\"get\\\",\\\"defaultCheck\\\":false,\\\"describe\\\":\\\"详情\\\"},{\\\"action\\\":\\\"query\\\",\\\"defaultCheck\\\":false,\\\"describe\\\":\\\"查询\\\"},{\\\"action\\\":\\\"update\\\",\\\"defaultCheck\\\":false,\\\"describe\\\":\\\"修改\\\"},{\\\"action\\\":\\\"delete\\\",\\\"defaultCheck\\\":false,\\\"describe\\\":\\\"删除\\\"}]\",\"actionEntitySet\":[{\"action\":\"add\",\"describe\":\"新增\",\"defaultCheck\":false},{\"action\":\"get\",\"describe\":\"详情\",\"defaultCheck\":false},{\"action\":\"query\",\"describe\":\"查询\",\"defaultCheck\":false},{\"action\":\"update\",\"describe\":\"修改\",\"defaultCheck\":false},{\"action\":\"delete\",\"describe\":\"删除\",\"defaultCheck\":false}],\"actionList\":null,\"dataAccess\":null},{\"roleId\":\"admin\",\"permissionId\":\"order\",\"permissionName\":\"订单管理\",\"actions\":\"[{\\\"action\\\":\\\"add\\\",\\\"defaultCheck\\\":false,\\\"describe\\\":\\\"新增\\\"},{\\\"action\\\":\\\"query\\\",\\\"defaultCheck\\\":false,\\\"describe\\\":\\\"查询\\\"},{\\\"action\\\":\\\"get\\\",\\\"defaultCheck\\\":false,\\\"describe\\\":\\\"详情\\\"},{\\\"action\\\":\\\"update\\\",\\\"defaultCheck\\\":false,\\\"describe\\\":\\\"修改\\\"},{\\\"action\\\":\\\"delete\\\",\\\"defaultCheck\\\":false,\\\"describe\\\":\\\"删除\\\"}]\",\"actionEntitySet\":[{\"action\":\"add\",\"describe\":\"新增\",\"defaultCheck\":false},{\"action\":\"query\",\"describe\":\"查询\",\"defaultCheck\":false},{\"action\":\"get\",\"describe\":\"详情\",\"defaultCheck\":false},{\"action\":\"update\",\"describe\":\"修改\",\"defaultCheck\":false},{\"action\":\"delete\",\"describe\":\"删除\",\"defaultCheck\":false}],\"actionList\":null,\"dataAccess\":null},{\"roleId\":\"admin\",\"permissionId\":\"permission\",\"permissionName\":\"权限管理\",\"actions\":\"[{\\\"action\\\":\\\"add\\\",\\\"defaultCheck\\\":false,\\\"describe\\\":\\\"新增\\\"},{\\\"action\\\":\\\"get\\\",\\\"defaultCheck\\\":false,\\\"describe\\\":\\\"详情\\\"},{\\\"action\\\":\\\"update\\\",\\\"defaultCheck\\\":false,\\\"describe\\\":\\\"修改\\\"},{\\\"action\\\":\\\"delete\\\",\\\"defaultCheck\\\":false,\\\"describe\\\":\\\"删除\\\"}]\",\"actionEntitySet\":[{\"action\":\"add\",\"describe\":\"新增\",\"defaultCheck\":false},{\"action\":\"get\",\"describe\":\"详情\",\"defaultCheck\":false},{\"action\":\"update\",\"describe\":\"修改\",\"defaultCheck\":false},{\"action\":\"delete\",\"describe\":\"删除\",\"defaultCheck\":false}],\"actionList\":null,\"dataAccess\":null},{\"roleId\":\"admin\",\"permissionId\":\"role\",\"permissionName\":\"角色管理\",\"actions\":\"[{\\\"action\\\":\\\"add\\\",\\\"defaultCheck\\\":false,\\\"describe\\\":\\\"新增\\\"},{\\\"action\\\":\\\"get\\\",\\\"defaultCheck\\\":false,\\\"describe\\\":\\\"详情\\\"},{\\\"action\\\":\\\"update\\\",\\\"defaultCheck\\\":false,\\\"describe\\\":\\\"修改\\\"},{\\\"action\\\":\\\"delete\\\",\\\"defaultCheck\\\":false,\\\"describe\\\":\\\"删除\\\"}]\",\"actionEntitySet\":[{\"action\":\"add\",\"describe\":\"新增\",\"defaultCheck\":false},{\"action\":\"get\",\"describe\":\"详情\",\"defaultCheck\":false},{\"action\":\"update\",\"describe\":\"修改\",\"defaultCheck\":false},{\"action\":\"delete\",\"describe\":\"删除\",\"defaultCheck\":false}],\"actionList\":null,\"dataAccess\":null},{\"roleId\":\"admin\",\"permissionId\":\"table\",\"permissionName\":\"桌子管理\",\"actions\":\"[{\\\"action\\\":\\\"add\\\",\\\"defaultCheck\\\":false,\\\"describe\\\":\\\"新增\\\"},{\\\"action\\\":\\\"get\\\",\\\"defaultCheck\\\":false,\\\"describe\\\":\\\"详情\\\"},{\\\"action\\\":\\\"query\\\",\\\"defaultCheck\\\":false,\\\"describe\\\":\\\"查询\\\"},{\\\"action\\\":\\\"update\\\",\\\"defaultCheck\\\":false,\\\"describe\\\":\\\"修改\\\"},{\\\"action\\\":\\\"delete\\\",\\\"defaultCheck\\\":false,\\\"describe\\\":\\\"删除\\\"}]\",\"actionEntitySet\":[{\"action\":\"add\",\"describe\":\"新增\",\"defaultCheck\":false},{\"action\":\"get\",\"describe\":\"详情\",\"defaultCheck\":false},{\"action\":\"query\",\"describe\":\"查询\",\"defaultCheck\":false},{\"action\":\"update\",\"describe\":\"修改\",\"defaultCheck\":false},{\"action\":\"delete\",\"describe\":\"删除\",\"defaultCheck\":false}],\"actionList\":null,\"dataAccess\":null},{\"roleId\":\"admin\",\"permissionId\":\"user\",\"permissionName\":\"用户管理\",\"actions\":\"[{\\\"action\\\":\\\"add\\\",\\\"defaultCheck\\\":false,\\\"describe\\\":\\\"新增\\\"},{\\\"action\\\":\\\"import\\\",\\\"defaultCheck\\\":false,\\\"describe\\\":\\\"导入\\\"},{\\\"action\\\":\\\"get\\\",\\\"defaultCheck\\\":false,\\\"describe\\\":\\\"详情\\\"},{\\\"action\\\":\\\"update\\\",\\\"defaultCheck\\\":false,\\\"describe\\\":\\\"修改\\\"},{\\\"action\\\":\\\"delete\\\",\\\"defaultCheck\\\":false,\\\"describe\\\":\\\"删除\\\"},{\\\"action\\\":\\\"export\\\",\\\"defaultCheck\\\":false,\\\"describe\\\":\\\"导出\\\"}]\",\"actionEntitySet\":[{\"action\":\"add\",\"describe\":\"新增\",\"defaultCheck\":false},{\"action\":\"import\",\"describe\":\"导入\",\"defaultCheck\":false},{\"action\":\"get\",\"describe\":\"详情\",\"defaultCheck\":false},{\"action\":\"update\",\"describe\":\"修改\",\"defaultCheck\":false},{\"action\":\"delete\",\"describe\":\"删除\",\"defaultCheck\":false},{\"action\":\"export\",\"describe\":\"导出\",\"defaultCheck\":false}],\"actionList\":null,\"dataAccess\":null}]}");
        return vo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        return null;
    }

}
