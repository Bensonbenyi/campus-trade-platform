package com.campus.trade.enums;

import lombok.Getter;

/**
 * 商品成色 对应product.condition_level
 */
@Getter
public enum ConditionEnum {
    NEW("NEW", "全新"),
    LIKE_NEW("LIKE_NEW", "几乎全新"),
    USED("USED", "有使用痕迹"),
    OLD("OLD", "老旧");

    private final String code;
    private final String desc;

    ConditionEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}