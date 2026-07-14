package com.campus.trade.dto;

import lombok.Data;

@Data
public class UserRegisterDTO {
    private String phone;
    private String code;
    private String password;
    private String studentId;
    private String realName;
}
