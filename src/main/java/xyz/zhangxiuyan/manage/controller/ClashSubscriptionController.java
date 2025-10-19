package xyz.zhangxiuyan.manage.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import xyz.zhangxiuyan.manage.service.ClashSubscriptionService;

import javax.annotation.Resource;

/**
 * @author zxy
 * @version 1.0 - 2024/1/30
 */
@RestController
@RequestMapping("/subscription")
public class ClashSubscriptionController {

    @Resource
    private ClashSubscriptionService clashSubscriptionService;

    @GetMapping("/subscription-yml")
    public String getSubscriptionString(@RequestParam("token") String token) {
        return clashSubscriptionService.getSubscriptionString(token);
    }

}
