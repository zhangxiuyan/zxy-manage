package xyz.zhangxiuyan.manage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;
import xyz.zhangxiuyan.common.exception.BusinessException;
import xyz.zhangxiuyan.manage.common.Constants.SystemConstant;
import xyz.zhangxiuyan.manage.entity.SysRouter;
import xyz.zhangxiuyan.manage.entity.dto.SysRouterDTO;
import xyz.zhangxiuyan.manage.entity.vo.SysRouterVO;
import xyz.zhangxiuyan.manage.mapper.SysRouterMapper;
import xyz.zhangxiuyan.manage.service.SysRouterService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <p>
 * 路由 服务实现类
 * </p>
 *
 * @author zxy
 * @since 2024-07-18
 */
@Service
public class SysRouterServiceImpl extends ServiceImpl<SysRouterMapper, SysRouter> implements SysRouterService {

    @Override
    public IPage<SysRouterVO> queryRouterPage(SysRouterDTO sysRouterQuery) {
        if (sysRouterQuery.getPageNum() == null || sysRouterQuery.getPageSize() == null) {
            throw new IllegalArgumentException("Page number and page size can not be null");
        }
        Page<SysRouter> page = new Page<>(sysRouterQuery.getPageNum(), sysRouterQuery.getPageSize());
        LambdaQueryWrapper<SysRouter> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysRouter::getDeleteMark, 0).orderByDesc(SysRouter::getCreateTime);
        page = page(page, queryWrapper);
        Page<SysRouterVO> result = new Page<>();
        BeanUtils.copyProperties(page, result);
        List<SysRouterVO> vos = page.getRecords().stream().map(e -> {
            SysRouterVO vo = new SysRouterVO();
            BeanUtils.copyProperties(e, vo);
            return vo;
        }).collect(Collectors.toList());
        result.setRecords(vos);
        return result;
    }

    @Override
    public List<SysRouterVO> queryRouterList(SysRouterDTO sysRouterQuery) {
        LambdaQueryWrapper<SysRouter> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysRouter::getDeleteMark, 0).orderByDesc(SysRouter::getCreateTime);
        List<SysRouter> routers = baseMapper.selectList(queryWrapper);
        List<SysRouterVO> vos = routers.stream().map(e -> {
            SysRouterVO vo = new SysRouterVO();
            BeanUtils.copyProperties(e, vo);
            return vo;
        }).collect(Collectors.toList());
        return tree(vos);
    }

    @Override
    public void createRouter(SysRouterVO sysRouterVo) {
        if (sysRouterVo == null) {
            throw new IllegalArgumentException("router cannot be null");
        }
        SysRouter router = new SysRouter();
        BeanUtils.copyProperties(sysRouterVo, router);
        if (!save(router)) {
            throw new BusinessException("save router failed!");
        }
    }

    @Override
    public void deleteRouter(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("router id cannot be null");
        }
        LambdaUpdateWrapper<SysRouter> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(SysRouter::getDeleteMark, 1)
                .eq(SysRouter::getId, id);
        if (!update(updateWrapper)) {
            throw new BusinessException("delete router failed!");
        }
    }

    @Override
    public void updateRouter(Long id, SysRouterVO sysRouterVo) {
        if (id == null) {
            throw new IllegalArgumentException("router id cannot be null");
        }
        SysRouter router = new SysRouter();
        BeanUtils.copyProperties(sysRouterVo, router);
        router.setId(id);
        if (updateById(router)) {
            throw new BusinessException("update router failed!");
        }
    }

    @Override
    public SysRouterVO queryRouter(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("router id cannot be null");
        }
        SysRouter router = baseMapper.selectById(id);
        SysRouterVO vo = new SysRouterVO();
        BeanUtils.copyProperties(router, vo);
        return vo;
    }

    private List<SysRouterVO> tree(List<SysRouterVO> routerVos) {
        for (SysRouterVO vo : routerVos) {
            if (vo.getParentId() == SystemConstant.ROOT_ROUTE_ID) {
                continue;
            } else {
                Optional<SysRouterVO> first = routerVos.stream().filter(e -> e.getId().equals(vo.getParentId())).findFirst();
                if (!first.isPresent()) {
                    throw new BusinessException("The route resolution error and the rootless node exists");
                }
                SysRouterVO parent = first.get();
                if (parent.getChildren() == null) {
                    parent.setChildren(Collections.singletonList(vo));
                } else {
                    parent.getChildren().add(vo);
                }
                routerVos.remove(vo);
                tree(routerVos);
            }
        }
        return routerVos;
    }

}
