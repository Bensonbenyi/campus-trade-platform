package com.campus.trade.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.trade.vo.ProductVO;
import java.math.BigDecimal;
import java.util.List;

public interface SearchService {
    // 4.1 商品多条件搜索分页
    Page<ProductVO> searchProduct(String keyword, Long categoryId, BigDecimal minPrice, BigDecimal maxPrice,
                                   String conditionLevel, String tradeType, String sort, Integer page, Integer size);
    // 4.4 获取热门搜索词固定列表
    List<String> getHotSearchWord();
}