package com.campus.trade.enums;

import lombok.Getter;

/**
 * 交易方式 对应product.trade_type
 */
@Getter
public enum TradeTypeEnum {
    PICKUP("PICKUP", "自提"),
    DELIVERY("DELIVERY", "送货"),
    BOTH("BOTH", "两者均可");

    private final String code;
    private final String desc;

    TradeTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}