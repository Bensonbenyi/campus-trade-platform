package com.campus.trade.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.trade.common.Result;
import com.campus.trade.service.SearchService;
import com.campus.trade.vo.ProductVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/search")
@Tag(name = "搜索模块接口", description = "商品模糊搜索、多条件筛选、热门搜索词")
public class SearchController {
    @Resource
    private SearchService searchService;

    // 4.1 多条件商品搜索
    @GetMapping
    @Operation(summary = "商品关键词多条件搜索", description = "支持关键词、分类、价格区间、成色、交易方式筛选；排序：latest最新发布 / price_asc低价 / price_desc高价")
    public Result<Page<ProductVO>> search(
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "类目ID") @RequestParam(required = false) Long categoryId,
            @Parameter(description = "最低价格") @RequestParam(required = false) BigDecimal minPrice,
            @Parameter(description = "最高价格") @RequestParam(required = false) BigDecimal maxPrice,
            @Parameter(description = "成色 NEW/LIKE_NEW/USED/OLD") @RequestParam(required = false) String conditionLevel,
            @Parameter(description = "交易方式 PICKUP/DELIVERY/BOTH") @RequestParam(required = false) String tradeType,
            @Parameter(description = "排序规则，默认latest") @RequestParam(defaultValue = "latest") String sort,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "20") Integer size
    ) {
        Page<ProductVO> pageData = searchService.searchProduct(keyword, categoryId, minPrice, maxPrice,
                conditionLevel, tradeType, sort, page, size);
        return Result.success(pageData);
    }

    // 4.4 热门搜索词
    @GetMapping("/hot")
    @Operation(summary = "获取热门搜索词", description = "简单固定热门词列表")
    public Result<List<String>> hotWord() {
        List<String> hotList = searchService.getHotSearchWord();
        return Result.success(hotList);
    }
}