package com.campus.trade.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.ZonedDateTime;

/**
 * 类目表 category
 */
@Data
@TableName("category")
public class Category {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 父类目ID，一级类目为null */
    private Long parentId;

    /** 类目名称 */
    private String name;

    /** 图标 */
    private String icon;

    /** 排序值 */
    private Integer sortOrder;

    /** 状态 ACTIVE/DISABLED */
    private String status;

    private ZonedDateTime createdAt;

    private ZonedDateTime updatedAt;
}