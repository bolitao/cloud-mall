package xyz.bolitao.cloudmall.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import xyz.bolitao.cloudmall.product.model.entity.Product;

/**
 * @author boli.tao
 */
@FeignClient(value = "cloud-mall-category-product")
public interface ProductFeignClient {
    // TODO: com
    @GetMapping("product/detail4feign")
    Product detail4feign(@RequestParam Integer id);
}
