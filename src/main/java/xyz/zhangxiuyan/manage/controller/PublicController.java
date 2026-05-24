package xyz.zhangxiuyan.manage.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.zhangxiuyan.common.http.HttpResult;
import xyz.zhangxiuyan.manage.config.RsaKeyManager;

import javax.annotation.Resource;

/**
 *
 */
@RequestMapping("/public")
@RestController
public class PublicController {

    @Resource
    private RsaKeyManager rsaKeyManager;

    @GetMapping("/key")
    public HttpResult<String> getPublicKey() {
        return HttpResult.success(rsaKeyManager.getPublicKeyBase64());
    }

}
