package xyz.bolitao.cloudmall.order.controller;

import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.bolitao.cloudmall.common.common.ApiRestResponse;
import xyz.bolitao.cloudmall.order.model.vo.OrderVO;
import xyz.bolitao.cloudmall.order.service.OrderService;

/**
 * @author boli.tao
 */
@RequestMapping("/admin/order")
@RestController
public class OrderAdminController {
    private final OrderService orderService;

    @Autowired
    public OrderAdminController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/list")
    public ApiRestResponse<PageInfo<OrderVO>> list4admin(@RequestParam Integer pageNum,
                                                         @RequestParam Integer pageSize) {
        PageInfo<OrderVO> orderVOPageInfo = orderService.list4Admin(pageNum, pageSize);
        return ApiRestResponse.success(orderVOPageInfo);
    }

    @PostMapping("/delivered")
    public ApiRestResponse<String> deliver(@RequestParam String orderNo) {
        orderService.deliver(orderNo);
        return ApiRestResponse.success();
    }

    @PostMapping("/finish")
    public ApiRestResponse<String> finish(@RequestParam String orderNo) {
        orderService.finish(orderNo);
        return ApiRestResponse.success();
    }
}
