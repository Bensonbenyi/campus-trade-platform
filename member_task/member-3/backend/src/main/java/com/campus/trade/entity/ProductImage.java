package com.campus.trade.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.ZonedDateTime;

/**
 * 商品图片表 product_image
 */
@Data
@TableName("product_image")
public class ProductImage {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 商品id */
    private Long productId;

    /** 图片url */
    private String imageUrl;

    /** 排序 1~9 */
    private Integer sortOrder;

    /** 是否主图 */
    private Boolean isMain;

    private ZonedDateTime createdAt;
}