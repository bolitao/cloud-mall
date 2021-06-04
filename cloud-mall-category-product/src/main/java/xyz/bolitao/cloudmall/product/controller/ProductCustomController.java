package xyz.bolitao.cloudmall.product.controller;

import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import xyz.bolitao.cloudmall.common.common.ApiRestResponse;
import xyz.bolitao.cloudmall.product.model.dto.ProductListReqDTO;
import xyz.bolitao.cloudmall.product.model.entity.Product;
import xyz.bolitao.cloudmall.product.service.ProductService;

/**
 * @author boli.tao
 */
@RestController
@RequestMapping("/product")
public class ProductCustomController {
    private final ProductService productService;

    @Autowired
    public ProductCustomController(ProductService productService) {
        this.productService = productService;
    }

    @ApiOperation("前台 - 获取商品信息")
    @GetMapping("/detail")
    public ApiRestResponse<Product> detail(@RequestParam Integer id) {
        return ApiRestResponse.success(productService.detail4Custom(id));
    }

    @ApiOperation("前台 - 分页查询")
    @GetMapping("/list")
    public ApiRestResponse<PageInfo<Product>> list(ProductListReqDTO productListReqDTO) {
        PageInfo<Product> productPageInfo = productService.list4Custom(productListReqDTO);
        return ApiRestResponse.success(productPageInfo);
    }
}
