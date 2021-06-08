package xyz.bolitao.cloudmall.cart.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.bolitao.cloudmall.cart.model.vo.CartVO;
import xyz.bolitao.cloudmall.cart.service.CartService;
import xyz.bolitao.cloudmall.common.common.ApiRestResponse;
import xyz.bolitao.cloudmall.common.common.Constant;
import xyz.bolitao.cloudmall.common.exception.ImoocMallException;
import xyz.bolitao.cloudmall.common.exception.ImoocMallExceptionEnum;
import xyz.bolitao.cloudmall.feign.UserFeignClient;

import java.util.List;

/**
 * @author boli.tao
 */
@RestController
@RequestMapping("/cart")
public class CartController {
    private final CartService cartService;
    private final UserFeignClient userFeignClient;

    @Autowired
    public CartController(CartService cartService, UserFeignClient userFeignClient) {
        this.cartService = cartService;
        this.userFeignClient = userFeignClient;
    }

    @GetMapping("/list")
    @ApiOperation("购物车列表")
    public ApiRestResponse<List<CartVO>> list() {
        Integer userId = userFeignClient.getUser().getId();
        List<CartVO> carts = cartService.list(userId);
        return ApiRestResponse.success(carts);
    }

    @PostMapping("/add")
    public ApiRestResponse<List<CartVO>> add(@RequestParam Integer productId, @RequestParam Integer count) {
        Integer userId = userFeignClient.getUser().getId();
        List<CartVO> addResult = cartService.add(userId, productId, count);
        return ApiRestResponse.success(addResult);
    }

    @PostMapping("/update")
    public ApiRestResponse<List<CartVO>> update(@RequestParam Integer productId, @RequestParam Integer count) {
        List<CartVO> cartVOS = cartService.update(userFeignClient.getUser().getId(), productId, count);
        return ApiRestResponse.success(cartVOS);
    }

    @PostMapping("/delete")
    public ApiRestResponse<List<CartVO>> delete(@RequestParam Integer productId) {
        List<CartVO> cartVOS = cartService.delete(userFeignClient.getUser().getId(), productId);
        return ApiRestResponse.success(cartVOS);
    }

    @PostMapping("/select")
    public ApiRestResponse<List<CartVO>> select(@RequestParam Integer productId, @RequestParam Integer selected) {
        if (!Constant.Cart.CART_CHECK_OR_NOT.contains(selected)) {
            throw new ImoocMallException(ImoocMallExceptionEnum.REQUEST_PARAM_ERROR);
        }
        List<CartVO> cartVOS = cartService.selectOrNot(userFeignClient.getUser().getId(), productId, selected);
        return ApiRestResponse.success(cartVOS);
    }

    @PostMapping("/selectAll")
    public ApiRestResponse<List<CartVO>> selectAll(@RequestParam Integer selected) {
        if (!Constant.Cart.CART_CHECK_OR_NOT.contains(selected)) {
            throw new ImoocMallException(ImoocMallExceptionEnum.REQUEST_PARAM_ERROR);
        }
        List<CartVO> cartVOS = cartService.selectAllOrAllNot(userFeignClient.getUser().getId(), selected);
        return ApiRestResponse.success(cartVOS);
    }
}
