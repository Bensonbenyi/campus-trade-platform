package com.campus.trade.controller;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.trade.common.Result;
import com.campus.trade.dto.ProductPublishDTO;
import com.campus.trade.service.ProductService;
import com.campus.trade.vo.ProductVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/product")
@Tag(name = "商品模块接口", description = "商品发布、编辑、删除、上下架、查询相关接口")
public class ProductController {
    @Resource
    private ProductService productService;

    // 2.4 发布商品 POST /api/product
    @PostMapping
    @Operation(summary = "发布商品", description = "标题2-50字，描述10-1000字，图片1-9张，每日最多发布5件，发布后状态为待审核(0)")
    public Result<String> publish(@Valid @RequestBody ProductPublishDTO dto) {
        Long loginUserId = 1L;
        productService.publishProduct(dto, loginUserId);
        return Result.success("商品发布成功");
    }

    // 2.5 商品详情 GET /api/product/{id}
    @GetMapping("/{id}")
    @Operation(summary = "获取商品详情", description = "返回商品基础信息、图片列表、卖家昵称")
    public Result<ProductVO> detail(@Parameter(description = "商品id") @PathVariable Long id) {
        ProductVO vo = productService.getProductDetail(id);
        return Result.success(vo);
    }

    // 2.6 首页商品分页 GET /api/product
    @GetMapping
    @Operation(summary = "首页商品分页列表", description = "仅查询已上架商品，支持分类筛选分页")
    public Result<Page<ProductVO>> list(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "20") Integer size,
            @Parameter(description = "类目id，非必传") @RequestParam(required = false) Long categoryId
    ) {
        Page<ProductVO> pageData = productService.getProductPage(categoryId, page, size);
        return Result.success(pageData);
    }

    // 2.7 我的发布分页 GET /api/product/my
    @GetMapping("/my")
    @Operation(summary = "我的商品列表", description = "查询当前用户发布商品，status传数字：0待审核 1已上架 2审核失败 3已下架")
    public Result<Page<ProductVO>> myProductList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "20") Integer size,
            @Parameter(description = "商品状态数字，非必传") @RequestParam(required = false) Integer status
    ) {
        Long loginUserId = 1L;
        Page<ProductVO> myPage = productService.getMyProductPage(loginUserId, status, page, size);
        return Result.success(myPage);
    }

    // 3.1 编辑商品 PUT /api/product/{id}
    @PutMapping("/{id}")
    @Operation(summary = "编辑商品", description = "仅本人商品可编辑；仅待审核/审核失败/已下架商品支持编辑，已上架不可修改")
    public Result<String> edit(
            @Parameter(description = "商品id") @PathVariable Long id,
            @Valid @RequestBody ProductPublishDTO dto
    ) {
        Long loginUserId = 1L;
        productService.editProduct(id, dto, loginUserId);
        return Result.success("商品编辑完成");
    }

    // 3.2 删除商品 DELETE /api/product/{id}
    @DeleteMapping("/{id}")
    @Operation(summary = "删除商品", description = "仅商品发布者可执行逻辑删除")
    public Result<String> delete(@Parameter(description = "商品id") @PathVariable Long id) {
        Long loginUserId = 1L;
        productService.deleteProduct(id, loginUserId);
        return Result.success("商品删除成功");
    }

    // 3.3 商品上下架 PUT /api/product/{id}/status
    @PutMapping("/{id}/status")
    @Operation(summary = "商品上下架", description = "targetStatus=1上架、targetStatus=3下架；仅本人操作")
    public Result<String> changeStatus(
            @Parameter(description = "商品id") @PathVariable Long id,
            @Parameter(description = "目标状态：1上架，3下架") @RequestParam Integer targetStatus
    ) {
        Long loginUserId = 1L;
        productService.changeProductStatus(id, targetStatus, loginUserId);
        return Result.success("商品状态修改完成");
    }
}