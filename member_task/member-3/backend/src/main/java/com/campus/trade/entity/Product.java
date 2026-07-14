package com.campus.trade.entity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
/**
 * 商品表 product
 */
@Data
@TableName("product")
public class Product {
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 卖家id */
    private Long sellerId;
    /** 类目id */
    private Long categoryId;
    /** 标题 2-80字 */
    private String title;
    /** 描述 10-5000字 */
    private String description;
    /** 售价 */
    private BigDecimal price;
    /** 原价 */
    private BigDecimal originalPrice;
    /** 成色 NEW/LIKE_NEW/USED/OLD */
    private String conditionLevel;
    /** 交易方式 PICKUP/DELIVERY/BOTH */
    private String tradeType;
    /** 交易地点备注 */
    private String tradeLocation;
    /** 商品状态：0待审核 1已上架 2审核失败 3已下架 */
    private Integer status;
    /** 浏览量 */
    private Integer viewCount;
    /** 收藏数 */
    private Integer favoriteCount;
    /** 上架时间 */
    private ZonedDateTime publishedAt;
    /** 成交时间 */
    private ZonedDateTime soldAt;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
    /** 软删除时间 */
    private ZonedDateTime deletedAt;
}