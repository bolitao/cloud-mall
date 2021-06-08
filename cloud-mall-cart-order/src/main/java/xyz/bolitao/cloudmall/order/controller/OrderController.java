package xyz.bolitao.cloudmall.order.controller;

import com.github.pagehelper.PageInfo;
import com.google.zxing.WriterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.bolitao.cloudmall.common.common.ApiRestResponse;
import xyz.bolitao.cloudmall.order.model.dto.CreateOrderReqDTO;
import xyz.bolitao.cloudmall.order.model.vo.OrderVO;
import xyz.bolitao.cloudmall.order.service.OrderService;

import java.io.IOException;

/**
 * @author boli.tao
 */
@RestController
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/create")
    public ApiRestResponse<String> create(@RequestBody CreateOrderReqDTO createOrderReqDTO) {
        String orderNo = orderService.create(createOrderReqDTO);
        return ApiRestResponse.success(orderNo);
    }

    @GetMapping("/detail")
    public ApiRestResponse<OrderVO> detail(@RequestParam String orderNo) {
        OrderVO detail = orderService.detail(orderNo);
        return ApiRestResponse.success(detail);
    }

    @GetMapping("/list")
    public ApiRestResponse<PageInfo<OrderVO>> list(@RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        PageInfo<OrderVO> orderVOPageInfo = orderService.list4Customer(pageNum, pageSize);
        return ApiRestResponse.success(orderVOPageInfo);
    }

    @PostMapping("/cancel")
    public ApiRestResponse<String> cancel(@RequestParam String orderNo) {
        orderService.cancel(orderNo);
        return ApiRestResponse.success();
    }

    @GetMapping("/qrcode")
    public ApiRestResponse<String> qrcode(@RequestParam String orderNo) throws IOException, WriterException {
        String qrcodePngAddress = orderService.qrcode(orderNo);
        return ApiRestResponse.success(qrcodePngAddress);
    }

    @GetMapping("/pay")
    public ApiRestResponse<String> pay(@RequestParam String orderNo) {
        orderService.pay(orderNo);
        return ApiRestResponse.success();
    }
}
