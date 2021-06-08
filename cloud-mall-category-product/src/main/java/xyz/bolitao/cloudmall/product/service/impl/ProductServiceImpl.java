package xyz.bolitao.cloudmall.product.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.bolitao.cloudmall.category.model.vo.CategoryVO;
import xyz.bolitao.cloudmall.category.service.CategoryService;
import xyz.bolitao.cloudmall.common.common.Constant;
import xyz.bolitao.cloudmall.common.exception.ImoocMallException;
import xyz.bolitao.cloudmall.common.exception.ImoocMallExceptionEnum;
import xyz.bolitao.cloudmall.product.model.dao.ProductMapper;
import xyz.bolitao.cloudmall.product.model.dto.AddProductReqDTO;
import xyz.bolitao.cloudmall.product.model.dto.ProductListReqDTO;
import xyz.bolitao.cloudmall.product.model.entity.Product;
import xyz.bolitao.cloudmall.product.model.query.ProductListQuery;
import xyz.bolitao.cloudmall.product.service.ProductService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author boli.tao
 */
@Service
public class ProductServiceImpl implements ProductService {
    private final ProductMapper productMapper;
    private final CategoryService categoryService;

    @Autowired

    public ProductServiceImpl(ProductMapper productMapper, CategoryService categoryService) {
        this.productMapper = productMapper;
        this.categoryService = categoryService;
    }

    @Override
    public void add(AddProductReqDTO addProductReqDTO) {
        Product product = new Product();
        BeanUtils.copyProperties(addProductReqDTO, product);
        Product productOld = productMapper.selectByName(addProductReqDTO.getName());
        if (productOld != null) {
            throw new ImoocMallException(ImoocMallExceptionEnum.NAME_ALREADY_EXISTED);
        }

        int count = productMapper.insertSelective(product);
        if (count == 0) {
            throw new ImoocMallException(ImoocMallExceptionEnum.DB_INSERT_FAILED);
        }
    }

    @Override
    public void update(Product product) {
        Product oldProduct = productMapper.selectByName(product.getName());
        if (oldProduct != null && !oldProduct.getId().equals(product.getId())) {
            throw new ImoocMallException(ImoocMallExceptionEnum.NAME_ALREADY_EXISTED);
        }
        int count = productMapper.updateByPrimaryKeySelective(product);
        if (count == 0) {
            throw new ImoocMallException(ImoocMallExceptionEnum.UPDATE_FAILED);
        }
    }

    @Override
    public void delete(Integer id) {
        Product oldProduct = productMapper.selectByPrimaryKey(id);
        if (oldProduct == null) {
            throw new ImoocMallException(ImoocMallExceptionEnum.RECORD_NOT_EXISTS);
        }

        int count = productMapper.deleteByPrimaryKey(id);
        if (count == 0) {
            throw new ImoocMallException(ImoocMallExceptionEnum.DELETE_FAILED);
        }
    }

    @Override
    public void batchUpdateSellStatus(Integer[] ids, Integer sellStatus) {
        productMapper.batchUpdateSellStatus(ids, sellStatus);
    }

    @Override
    public PageInfo<Product> list4Admin(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return new PageInfo<>(productMapper.selectList4Admin());
    }

    @Override
    public Product detail4Custom(Integer id) {
        return productMapper.selectByPrimaryKey(id);
    }

    @Override
    public PageInfo<Product> list4Custom(ProductListReqDTO productListReqDTO) {
        ProductListQuery productListQuery = new ProductListQuery();

        // 搜索
        if (!Strings.isNullOrEmpty(productListReqDTO.getKeyword())) {
            String keyword = new StringBuilder().append("%")
                    .append(productListReqDTO.getKeyword())
                    .append("%").toString();
            productListQuery.setKeyword(keyword);
        }

        // 目录查询（包括此目录和子目录）
        if (productListReqDTO.getCategoryId() != null) {
            List<CategoryVO> categoryVOS = categoryService.listCategoryForCustom(productListReqDTO.getCategoryId());
            List<Integer> categoryIds = new ArrayList<>();
            categoryIds.add(productListReqDTO.getCategoryId());
            this.getCategoryIds(categoryVOS, categoryIds);
            productListQuery.setCategoryIds(categoryIds);
        }

        // 排序
        String orderBy = productListReqDTO.getOrderBy();
        if (Constant.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)) {
            PageHelper.startPage(productListReqDTO.getPageNum(), productListReqDTO.getPageSize(), orderBy);
        } else {
            PageHelper.startPage(productListReqDTO.getPageNum(), productListReqDTO.getPageSize());
        }

        List<Product> productList = productMapper.selectList4Custom(productListQuery);
        return new PageInfo<>(productList);
    }

    @Override
    public void updateStock(Integer productId, Integer stock) {
        Product product = new Product();
        product.setId(productId);
        product.setStock(stock);
        productMapper.updateByPrimaryKeySelective(product);
    }

    private void getCategoryIds(List<CategoryVO> categoryVOList, List<Integer> categoryIds) {
        // TODO: com
        for (CategoryVO categoryVO : categoryVOList) {
            if (categoryVO != null) {
                categoryIds.add(categoryVO.getId());
                getCategoryIds(categoryVO.getChildCategory(), categoryIds);
            }
        }
    }
}
