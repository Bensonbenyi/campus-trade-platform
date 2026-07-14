package com.campus.trade.vo;
import com.campus.trade.entity.ProductImage;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
@Data
public class ProductVO {
    private Long id;
    private String title;
    private BigDecimal price;
    private String desc;
    private Long categoryId;
    private Integer status;
    private Long userId;
    // 卖家昵称
    private String sellerName;
    // 商品图片列表
    private List<ProductImage> imageList;
}