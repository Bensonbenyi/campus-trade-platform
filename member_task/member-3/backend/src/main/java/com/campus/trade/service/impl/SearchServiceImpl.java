package com.campus.trade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.trade.entity.Product;
import com.campus.trade.enums.ProductStatusEnum;
import com.campus.trade.mapper.ProductMapper;
import com.campus.trade.service.SearchService;
import com.campus.trade.vo.ProductVO;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchServiceImpl implements SearchService {
    @Resource
    private ProductMapper productMapper;

    @Override
    public Page<ProductVO> searchProduct(String keyword, Long categoryId, BigDecimal minPrice, BigDecimal maxPrice,
                                         String conditionLevel, String tradeType, String sort, Integer page, Integer size) {
        Page<Product> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        // 只查已上架商品
        wrapper.eq(Product::getStatus, ProductStatusEnum.ON_SALE.getCode());

        // 关键词模糊搜索
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.like(Product::getTitle, keyword.trim());
        }
        // 分类筛选
        if (categoryId != null) {
            wrapper.eq(Product::getCategoryId, categoryId);
        }
        // 最低价格
        if (minPrice != null) {
            wrapper.ge(Product::getPrice, minPrice);
        }
        // 最高价格
        if (maxPrice != null) {
            wrapper.le(Product::getPrice, maxPrice);
        }
        // 成色筛选
        if (conditionLevel != null && !conditionLevel.isEmpty()) {
            wrapper.eq(Product::getConditionLevel, conditionLevel);
        }
        // 交易方式筛选
        if (tradeType != null && !tradeType.isEmpty()) {
            wrapper.eq(Product::getTradeType, tradeType);
        }

        // 排序规则
        switch (sort) {
            case "price_asc":
                wrapper.orderByAsc(Product::getPrice);
                break;
            case "price_desc":
                wrapper.orderByDesc(Product::getPrice);
                break;
            case "latest":
            default:
                wrapper.orderByDesc(Product::getCreatedAt);
                break;
        }

        Page<Product> productPage = productMapper.selectPage(pageParam, wrapper);
        Page<ProductVO> voPage = new Page<>();
        voPage.setTotal(productPage.getTotal());
        voPage.setRecords(productPage.getRecords().stream().map(this::convertVO).collect(Collectors.toList()));
        return voPage;
    }

    @Override
    public List<String> getHotSearchWord() {
        // 固定热门词列表
        return Arrays.asList("教材", "电动车", "宿舍收纳", "电脑配件", "运动器材", "考研资料");
    }

    // 实体转VO复用逻辑
    private ProductVO convertVO(Product product) {
        ProductVO vo = new ProductVO();
        vo.setId(product.getId());
        vo.setTitle(product.getTitle());
        vo.setPrice(product.getPrice());
        vo.setDesc(product.getDescription());
        vo.setCategoryId(product.getCategoryId());
        vo.setStatus(product.getStatus());
        vo.setUserId(product.getSellerId());
        return vo;
    }
}