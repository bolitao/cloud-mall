package xyz.bolitao.cloudmall.order.model.dao;

import org.springframework.stereotype.Repository;
import xyz.bolitao.cloudmall.order.model.entity.OrderItem;

import java.util.List;

@Repository
public interface OrderItemMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OrderItem record);

    int insertSelective(OrderItem record);

    OrderItem selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OrderItem record);

    int updateByPrimaryKey(OrderItem record);

    List<OrderItem> selectByOrderNum(String orderNo);
}