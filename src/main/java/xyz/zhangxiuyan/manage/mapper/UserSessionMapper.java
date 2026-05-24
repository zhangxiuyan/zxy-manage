package xyz.zhangxiuyan.manage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import xyz.zhangxiuyan.manage.entity.UserSession;

/**
 * 用户会话 Mapper
 *
 * @author zxy
 * @version 1.0 - 2025/10/23
 */
@Mapper
public interface UserSessionMapper extends BaseMapper<UserSession> {
}
