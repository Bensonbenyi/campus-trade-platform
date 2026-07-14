package com.campus.trade.controller;

import com.campus.trade.common.Result;
import com.campus.trade.service.CategoryService;
import com.campus.trade.vo.CategoryTreeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/product")
@Tag(name = "商品类目接口")
public class CategoryController {
    @Resource
    private CategoryService categoryService;

    @GetMapping("/category")
    @Operation(summary = "3.4 获取全类目树形数据")
    public Result<List<CategoryTreeVO>> getCategoryTree() {
        List<CategoryTreeVO> tree = categoryService.getCategoryTree();
        return Result.success(tree);
    }
}