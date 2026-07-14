package com.campus.trade.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserVO {
    private Long id;
    private String phone;
    private String nickname;
    private String avatar;
    private String studentId;
    private String realName;
    private String role;
    private String token;
}
