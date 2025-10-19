package xyz.zhangxiuyan.manage.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.zhangxiuyan.common.http.HttpResult;
import xyz.zhangxiuyan.manage.config.RsaKeyManager;

import javax.annotation.Resource;

/**
 * @author zxy
 * @version 1.0 - 2025/10/20
 */
@RestController
public class LoginController {

    @Resource
    private RsaKeyManager rsaKeyManager;

    @GetMapping("/public/key")
    public HttpResult<String> getPublicKey() {
        return HttpResult.success(rsaKeyManager.getPublicKeyBase64());
    }

}
