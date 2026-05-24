package xyz.zhangxiuyan.manage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import xyz.zhangxiuyan.common.exception.BusinessException;
import xyz.zhangxiuyan.manage.entity.ClashSubscription;
import xyz.zhangxiuyan.manage.mapper.ClashSubscriptionMapper;
import xyz.zhangxiuyan.manage.service.ClashSubscriptionService;
import xyz.zhangxiuyan.manage.service.ClashTokenService;

import jakarta.annotation.Resource;
import java.util.Base64;
import java.util.List;

/**
 * @author zxy
 * @version 1.0 - 2024/1/30
 */
@Service
public class ClashSubscriptionServiceImpl extends ServiceImpl<ClashSubscriptionMapper, ClashSubscription> implements ClashSubscriptionService {

    @Resource
    private ClashTokenService clashTokenService;

    @Override
    public String getSubscriptionString(String token) {
        if (StringUtils.isBlank(token) || !clashTokenService.validationToken(token)) {
            throw new BusinessException("invalid token");
        }
        QueryWrapper<ClashSubscription> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ClashSubscription::getDeleteMark, 0);
        List<ClashSubscription> subscriptions = baseMapper.selectList(queryWrapper);
        if (subscriptions == null) {
            throw new BusinessException("No subscriptions available");
        }
        ClashSubscription subscription = subscriptions.get(0);
        String context = subscription.getContext();
        if (context == null) {
            throw new BusinessException("No subscriptions available");
        }
        return context;
    }

}
