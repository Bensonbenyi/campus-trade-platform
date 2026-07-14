package com.campus.trade.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.trade.dto.ProductPublishDTO;
import com.campus.trade.entity.Product;
import com.campus.trade.entity.ProductImage;
import com.campus.trade.enums.ProductStatusEnum;
import com.campus.trade.mapper.ProductImageMapper;
import com.campus.trade.mapper.ProductMapper;
import com.campus.trade.service.ProductService;
import com.campus.trade.vo.ProductVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.annotation.Resource;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {
    @Resource
    private ProductImageMapper productImageMapper;
  
    @Override
    public int countTodayPublish(Long userId) {
        ZonedDateTime start = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS);
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getSellerId, userId)
                .ge(Product::getCreatedAt, start);
        return (int) this.count(wrapper);
    }
    @Override
    @Transactional
    public void publishProduct(ProductPublishDTO dto, Long userId) {
        int todayNum = countTodayPublish(userId);
        if (todayNum >= 5) {
            throw new RuntimeException("今日发布商品已达上限，最多发布5件");
        }
        Product product = new Product();
        product.setSellerId(userId);
        product.setTitle(dto.getTitle());
        product.setPrice(dto.getPrice());
        product.setDescription(dto.getDesc());
        product.setCategoryId(dto.getCategoryId());
        product.setStatus(ProductStatusEnum.PENDING_REVIEW.getCode());
        this.save(product);
        Long productId = product.getId();
        List<String> imgUrls = dto.getImgUrlList();
        int sort = 1;
        for (String url : imgUrls) {
            ProductImage image = new ProductImage();
            image.setProductId(productId);
            image.setImageUrl(url);
            image.setSortOrder(sort);
            image.setIsMain(sort == 1);
            productImageMapper.insert(image);
            sort++;
        }
    }
    @Override
    public ProductVO getProductDetail(Long productId) {
        Product product = this.getById(productId);
        ProductVO vo = convertToVO(product);
        LambdaQueryWrapper<ProductImage> imgWrapper = new LambdaQueryWrapper<>();
        imgWrapper.eq(ProductImage::getProductId, productId);
        List<ProductImage> imageList = productImageMapper.selectList(imgWrapper);
        vo.setImageList(imageList);
        vo.setSellerName("卖家昵称");
        return vo;
    }
    @Override
    public Page<ProductVO> getProductPage(Long categoryId, Integer page, Integer size) {
        Page<Product> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        if (categoryId != null) wrapper.eq(Product::getCategoryId, categoryId);
        wrapper.eq(Product::getStatus, ProductStatusEnum.ON_SALE.getCode());
        Page<Product> productPage = this.page(pageParam, wrapper);
        Page<ProductVO> voPage = new Page<>();
        voPage.setTotal(productPage.getTotal());
        voPage.setRecords(productPage.getRecords().stream().map(this::convertToVO).toList());
        return voPage;
    }
    @Override
    public Page<ProductVO> getMyProductPage(Long userId, Integer status, Integer page, Integer size) {
        Page<Product> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getSellerId, userId);
        if (status != null) wrapper.eq(Product::getStatus, status);
        Page<Product> productPage = this.page(pageParam, wrapper);
        Page<ProductVO> voPage = new Page<>();
        voPage.setTotal(productPage.getTotal());
        voPage.setRecords(productPage.getRecords().stream().map(this::convertToVO).toList());
        return voPage;
    }
   
    /**
     * 3.1 编辑商品
     * 限制：只能编辑自己的商品；仅状态0待审核、2审核失败、3已下架可编辑，已上架(1)不允许编辑
     */
    @Override
    @Transactional
    public void editProduct(Long productId, ProductPublishDTO dto, Long loginUserId) {
        Product product = this.getById(productId);
        if(product == null) throw new RuntimeException("商品不存在");
        // 校验1：只能编辑自己的商品
        if(!product.getSellerId().equals(loginUserId)) throw new RuntimeException("无权限编辑他人商品");
        // 校验2：状态限制 0待审核 /2审核失败 /3已下架 才能编辑；1已上架禁止编辑
        Integer status = product.getStatus();
        if(status == ProductStatusEnum.ON_SALE.getCode()){
            throw new RuntimeException("已上架商品不可编辑，请先下架后修改");
        }
        // 更新商品基础信息
        LambdaUpdateWrapper<Product> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Product::getId, productId)
                .set(Product::getTitle, dto.getTitle())
                .set(Product::getPrice, dto.getPrice())
                .set(Product::getDescription, dto.getDesc())
                .set(Product::getCategoryId, dto.getCategoryId());
        this.update(updateWrapper);
        // 清空旧图片，重新插入新图片
        LambdaQueryWrapper<ProductImage> imgDelWrapper = new LambdaQueryWrapper<>();
        imgDelWrapper.eq(ProductImage::getProductId, productId);
        productImageMapper.delete(imgDelWrapper);
        List<String> imgUrls = dto.getImgUrlList();
        int sort = 1;
        for(String url : imgUrls){
            ProductImage image = new ProductImage();
            image.setProductId(productId);
            image.setImageUrl(url);
            image.setSortOrder(sort);
            image.setIsMain(sort == 1);
            productImageMapper.insert(image);
            sort++;
        }
    }
    /**
     * 3.2 删除商品（逻辑删除，仅本人可删）
     */
    @Override
    @Transactional
    public void deleteProduct(Long productId, Long loginUserId) {
        Product product = this.getById(productId);
        if(product == null) throw new RuntimeException("商品不存在");
        if(!product.getSellerId().equals(loginUserId)) throw new RuntimeException("无权限删除他人商品");
        // 逻辑删除：填充删除时间
        LambdaUpdateWrapper<Product> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Product::getId, productId)
                .set(Product::getDeletedAt, ZonedDateTime.now());
        this.update(updateWrapper);
    }
    /**
     * 3.3 上下架状态切换
     * targetStatus=1上架 / targetStatus=3下架
     */
    @Override
    public void changeProductStatus(Long productId, Integer targetStatus, Long loginUserId) {
        Product product = this.getById(productId);
        if(product == null) throw new RuntimeException("商品不存在");
        if(!product.getSellerId().equals(loginUserId)) throw new RuntimeException("无权限操作他人商品");
        // 状态机规则校验
        Integer currStatus = product.getStatus();
        // 上架：仅待审核/审核失败/已下架可上架
        if(targetStatus == ProductStatusEnum.ON_SALE.getCode()){
            if(currStatus == ProductStatusEnum.ON_SALE.getCode()) throw new RuntimeException("商品已上架，无需重复操作");
        }
        // 下架：仅已上架商品能下架
        if(targetStatus == ProductStatusEnum.OFF_SHELF.getCode()){
            if(currStatus != ProductStatusEnum.ON_SALE.getCode()) throw new RuntimeException("仅已上架商品支持下架操作");
        }
        // 更新状态
        LambdaUpdateWrapper<Product> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Product::getId, productId).set(Product::getStatus, targetStatus);
        this.update(updateWrapper);
    }
    // 实体转换工具
    private ProductVO convertToVO(Product product) {
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