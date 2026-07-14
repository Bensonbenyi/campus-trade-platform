package com.campus.trade.common.result;

import lombok.Getter;

@Getter
public enum ResultCode {
    SUCCESS(200, "成功"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未登录或登录已过期"),
    FORBIDDEN(403, "无权执行此操作"),
    NOT_FOUND(404, "资源不存在"),
    FILE_TYPE_NOT_ALLOWED(4001, "仅支持 jpg、jpeg、png、webp 图片"),
    FILE_TOO_LARGE(4002, "单张图片不能超过 5MB"),
    INTERNAL_ERROR(500, "系统异常");

    private final int code;
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
