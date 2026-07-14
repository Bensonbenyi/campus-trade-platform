package com.campus.trade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.trade.entity.Category;
import com.campus.trade.vo.CategoryTreeVO;
import java.util.List;

public interface CategoryService extends IService<Category> {
    // 3.4 获取完整类目树
    List<CategoryTreeVO> getCategoryTree();
}