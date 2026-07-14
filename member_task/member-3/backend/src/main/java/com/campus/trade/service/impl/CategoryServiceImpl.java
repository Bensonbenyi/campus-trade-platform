package com.campus.trade.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.trade.entity.Category;
import com.campus.trade.mapper.CategoryMapper;
import com.campus.trade.service.CategoryService;
import com.campus.trade.vo.CategoryTreeVO;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Resource
    private CategoryMapper categoryMapper;
    @Override
    public List<CategoryTreeVO> getCategoryTree() {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Category::getSortOrder);
        // 只查询启用类目
        wrapper.eq(Category::getStatus, "ACTIVE");
        List<Category> allList = this.list(wrapper);
        // 筛选一级类目（parentId为null或0）
        List<CategoryTreeVO> rootList = allList.stream()
                .filter(item -> item.getParentId() == null || item.getParentId() == 0)
                .map(parent -> {
                    CategoryTreeVO vo = convert(parent);
                    // 挂载二级子类目
                    vo.setChildren(allList.stream()
                            .filter(child -> child.getParentId().equals(parent.getId()))
                            .map(this::convert)
                            .collect(Collectors.toList()));
                    return vo;
                }).collect(Collectors.toList());
        return rootList;
    }
    /**
     * 实体转为树形VO
     */
    private CategoryTreeVO convert(Category category) {
        CategoryTreeVO vo = new CategoryTreeVO();
        vo.setId(category.getId());
        vo.setCategoryName(category.getName());
        vo.setParentId(category.getParentId());
        return vo;
    }
}