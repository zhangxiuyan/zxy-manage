package xyz.zhangxiuyan.manage.entity.dto;

/**
 * @author zxy
 * @version 1.0 - 2023/6/12
 */
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

    public CommonDTO() {
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getSearchValue() {
        return searchValue;
    }

    public void setSearchValue(String searchValue) {
        this.searchValue = searchValue;
    }

}
