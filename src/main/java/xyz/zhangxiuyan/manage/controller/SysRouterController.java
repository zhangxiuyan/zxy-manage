package xyz.zhangxiuyan.manage.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.web.bind.annotation.*;
import xyz.zhangxiuyan.common.http.HttpResult;
import xyz.zhangxiuyan.manage.entity.dto.SysRouterDTO;
import xyz.zhangxiuyan.manage.entity.vo.SysRouterVO;
import xyz.zhangxiuyan.manage.service.SysRouterService;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zxy
 * @since 2024-07-18
 */
@RestController
@RequestMapping("/sys/router")
public class SysRouterController {
    
    @Resource
    private SysRouterService sysRouterService;
    
    @GetMapping("/")
    public HttpResult<IPage<SysRouterVO>> queryRouterPage(SysRouterDTO sysRouterQuery) {
        return HttpResult.success(sysRouterService.queryRouterPage(sysRouterQuery));
    }

    @GetMapping("/list")
    public HttpResult<List<SysRouterVO>> queryRouterList(SysRouterDTO sysRouterQuery) {
        return HttpResult.success(sysRouterService.queryRouterList(sysRouterQuery));
    }

    @PostMapping("/create")
    public HttpResult<String> createRouter(@RequestBody SysRouterVO sysRouterVo) {
        sysRouterService.createRouter(sysRouterVo);
        return HttpResult.success("success");
    }

    @DeleteMapping("/{id}")
    public HttpResult<String> deleteRouter(@PathVariable("id") Long id) {
        sysRouterService.deleteRouter(id);
        return HttpResult.success("success");
    }

    @PutMapping("/{id}")
    public HttpResult<String> updateRouter(@PathVariable("id") Long id, @RequestBody SysRouterVO sysRouterVo) {
        sysRouterService.updateRouter(id, sysRouterVo);
        return HttpResult.success("success");
    }

    @GetMapping("/{id}")
    public HttpResult<SysRouterVO> queryRouter(@PathVariable("id") Long id) {
        return HttpResult.success(sysRouterService.queryRouter(id));
    }

}
