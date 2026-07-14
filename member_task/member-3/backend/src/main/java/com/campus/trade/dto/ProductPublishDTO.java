package com.campus.trade.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductPublishDTO {
    @NotBlank(message = "商品标题不能为空")
    @Size(min = 2, max = 50, message = "标题长度必须在2‑50字之间")
    private String title;

    private BigDecimal price;

    @NotBlank(message = "商品描述不能为空")
    @Size(min = 10, max = 1000, message = "描述长度必须在10‑1000字之间")
    private String desc;

    private Long categoryId;

    @Size(min = 1, max = 9, message = "图片数量必须为1‑9张")
    private List<String> imgUrlList;
}