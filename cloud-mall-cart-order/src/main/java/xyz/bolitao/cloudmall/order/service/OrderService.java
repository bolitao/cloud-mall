package xyz.bolitao.cloudmall.order.service;

import com.github.pagehelper.PageInfo;
import com.google.zxing.WriterException;
import xyz.bolitao.cloudmall.order.model.dto.CreateOrderReqDTO;
import xyz.bolitao.cloudmall.order.model.vo.OrderVO;

import java.io.IOException;

/**
 * @author boli.tao
 */
public interface OrderService {
    String create(CreateOrderReqDTO createOrderReqDTO);

    OrderVO detail(String orderNo);

    PageInfo<OrderVO> list4Customer(Integer pageNum, Integer pageSize);

    void cancel(String orderNo);

    String qrcode(String orderNo) throws IOException, WriterException;

    PageInfo<OrderVO> list4Admin(Integer pageNum, Integer pageSize);

    void pay(String orderNo);

    void deliver(String orderNo);

    void finish(String orderNum);
}
