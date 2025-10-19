package xyz.zhangxiuyan.manage.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.zhangxiuyan.manage.service.ClashTokenService;

import javax.annotation.Resource;

/**
 * @author zxy
 * @version 1.0 - 2024/1/31
 */
@RestController
@RequestMapping("/clash-token")
public class ClashTokenController {

    @Resource
    private ClashTokenService clashTokenService;

}
