package com.campus.trade.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user_auth")
public class UserAuth {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String authStatus;
    private String studentId;
    private String realName;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
