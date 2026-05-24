package xyz.zhangxiuyan.manage.entity.dto;

import lombok.Data;
/**
 * @author zxy
 * @version 1.0 - 2023/6/12
 */
@Data
public class CommonDTO {
    
    /**
     * 页码
     */
    private Integer pageNum;

    /**
     * limit
     */
    private Integer pageSize;

    /**
     * 模糊搜索
     */
    private String searchValue;

}
