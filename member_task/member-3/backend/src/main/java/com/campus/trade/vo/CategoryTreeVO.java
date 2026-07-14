package com.campus.trade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

@Data
@Schema(description = "类目树形结构")
public class CategoryTreeVO {
    @Schema(description = "类目id")
    private Long id;
    @Schema(description = "类目名称")
    private String categoryName;
    @Schema(description = "父级类目id")
    private Long parentId;
    @Schema(description = "子类目列表")
    private List<CategoryTreeVO> children;
}