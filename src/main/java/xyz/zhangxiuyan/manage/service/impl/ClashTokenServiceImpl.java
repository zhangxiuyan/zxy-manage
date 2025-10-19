package xyz.zhangxiuyan.manage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import xyz.zhangxiuyan.manage.entity.ClashToken;
import xyz.zhangxiuyan.manage.mapper.ClashTokenMapper;
import xyz.zhangxiuyan.manage.service.ClashTokenService;

import java.time.LocalDateTime;

/**
 * @author zxy
 * @version 1.0 - 2024/1/31
 */
@Service
public class ClashTokenServiceImpl extends ServiceImpl<ClashTokenMapper, ClashToken> implements ClashTokenService {

    @Override
    public boolean validationToken(String token) {
        QueryWrapper<ClashToken> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ClashToken::getDeleteMark, 0)
                .eq(ClashToken::getToken, token)
                .gt(ClashToken::getFailureTime, LocalDateTime.now());
        ClashToken one = getOne(queryWrapper);
        return one != null;
    }

}
