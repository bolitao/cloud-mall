package xyz.bolitao.cloudmall.product.model.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import xyz.bolitao.cloudmall.product.model.query.ProductListQuery;
import xyz.bolitao.cloudmall.product.model.entity.Product;

import java.util.List;

@Repository
public interface ProductMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Product record);

    int insertSelective(Product record);

    Product selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKey(Product record);

    Product selectByName(String name);

    void batchUpdateSellStatus(@Param("ids") Integer[] ids, @Param("sellStatus") Integer sellStatus);

    List<Product> selectList4Admin();

    List<Product> selectList4Custom(@Param("query") ProductListQuery productListQuery);
}
