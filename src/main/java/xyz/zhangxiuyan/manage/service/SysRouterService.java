package xyz.zhangxiuyan.manage.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import xyz.zhangxiuyan.manage.entity.SysRouter;
import com.baomidou.mybatisplus.extension.service.IService;
import xyz.zhangxiuyan.manage.entity.dto.SysRouterDTO;
import xyz.zhangxiuyan.manage.entity.vo.SysRouterVO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zxy
 * @since 2024-07-18
 */
public interface SysRouterService extends IService<SysRouter> {

    IPage<SysRouterVO> queryRouterPage(SysRouterDTO sysRouterQuery);

    void createRouter(SysRouterVO sysRouterVo);

    void deleteRouter(Long id);

    void updateRouter(Long id, SysRouterVO sysRouterVo);

    SysRouterVO queryRouter(Long id);

    List<SysRouterVO> queryRouterList(SysRouterDTO sysRouterQuery);

}
