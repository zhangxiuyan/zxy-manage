package xyz.zhangxiuyan.manage.common.Enums;

public enum SystemParameterEnum {

    INVALID(-1, "INVALID", "无效"),

    ON(0, "ON", "开启"),

    OFF(1, "OFF", "关闭");

    private Integer code;

    private String value;

    private String desc;

    SystemParameterEnum(Integer code, String value, String desc) {
        this.code = code;
        this.value = value;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }

}
