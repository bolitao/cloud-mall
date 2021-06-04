package xyz.bolitao.cloudmall.product.service;

import com.github.pagehelper.PageInfo;
import xyz.bolitao.cloudmall.product.model.dto.AddProductReqDTO;
import xyz.bolitao.cloudmall.product.model.dto.ProductListReqDTO;
import xyz.bolitao.cloudmall.product.model.entity.Product;

/**
 * @author boli.tao
 */
public interface ProductService {
    void add(AddProductReqDTO addProductReqDTO);

    void update(Product product);

    void delete(Integer id);

    void batchUpdateSellStatus(Integer[] ids, Integer sellStatus);

    PageInfo<Product> list4Admin(Integer pageNum, Integer pageSize);

    Product detail4Custom(Integer id);

    PageInfo<Product> list4Custom(ProductListReqDTO productListReqDTO);
}
