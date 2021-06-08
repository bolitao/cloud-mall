package xyz.bolitao.cloudmall.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import xyz.bolitao.cloudmall.product.model.entity.Product;

/**
 * @author boli.tao
 */
@FeignClient(contextId = "cloud-mall-category-product-client", value = "cloud-mall-category-product")
public interface ProductFeignClient {
    // TODO: com
    @GetMapping("/product/detail4feign")
    Product detail4feign(@RequestParam Integer id);

    @GetMapping("/product/updateStock")
    void updateStock(@RequestParam Integer productId, @RequestParam Integer stock);
}
