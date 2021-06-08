package xyz.bolitao.cloudmall.order.model.dao;

import org.springframework.stereotype.Repository;
import xyz.bolitao.cloudmall.order.model.entity.Order;

import java.util.List;

@Repository
public interface OrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Order record);

    int insertSelective(Order record);

    Order selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);

    Order selectByOrderNum(String orderNo);

    List<Order> select4Custom(Integer userId);

    List<Order> selectAll4Admin();
}