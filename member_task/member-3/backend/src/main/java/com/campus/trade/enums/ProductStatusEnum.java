package com.campus.trade.enums;

import lombok.Getter;

/**
 * 商品状态 数字编码，贴合本次状态机
 * 0：待审核
 * 1：已上架
 * 2：审核失败
 * 3：已下架
 * 7：违规下架
 * 8：已删除
 */
@Getter
public enum ProductStatusEnum {
    PENDING_REVIEW(0, "PENDING_REVIEW", "待审核"),
    ON_SALE(1, "ON_SALE", "已上架"),
    REJECTED(2, "REJECTED", "审核失败"),
    OFF_SHELF(3, "OFF_SHELF", "已下架"),
    VIOLATION_DELISTED(7, "VIOLATION_DELISTED", "违规下架"),
    DELETED(8, "DELETED", "已删除");

    private final Integer code;
    private final String strCode;
    private final String desc;

    ProductStatusEnum(Integer code, String strCode, String desc) {
        this.code = code;
        this.strCode = strCode;
        this.desc = desc;
    }
}