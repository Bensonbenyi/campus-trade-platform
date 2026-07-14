package com.campus.trade.service;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.trade.dto.ProductPublishDTO;
import com.campus.trade.entity.Product;
import com.campus.trade.vo.ProductVO;
public interface ProductService extends IService<Product> {
    // Day2 原有方法
    void publishProduct(ProductPublishDTO dto, Long userId);
    int countTodayPublish(Long userId);
    ProductVO getProductDetail(Long productId);
    Page<ProductVO> getProductPage(Long categoryId, Integer page, Integer size);
    Page<ProductVO> getMyProductPage(Long userId, Integer status, Integer page, Integer size);
    // ===== Day3 新增3.1/3.2/3.3 业务方法 =====
    // 3.1 编辑商品
    void editProduct(Long productId, ProductPublishDTO dto, Long loginUserId);
    // 3.2 逻辑删除商品
    void deleteProduct(Long productId, Long loginUserId);
    // 3.3 修改商品上下架状态
    void changeProductStatus(Long productId, Integer targetStatus, Long loginUserId);
}