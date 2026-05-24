package xyz.zhangxiuyan.manage.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@TableName("clash_token")
public class ClashToken implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    private Long recordId;

    private Long userId;

    private String token;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private LocalDateTime failureTime;

    private Integer deleteMark;
}
