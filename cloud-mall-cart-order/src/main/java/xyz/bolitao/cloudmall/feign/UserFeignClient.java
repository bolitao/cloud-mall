package xyz.bolitao.cloudmall.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import xyz.bolitao.cloudmall.user.model.entity.User;

@FeignClient(value = "cloud-mall-user")
public interface UserFeignClient {
    @GetMapping("getUser")
    User getUser();
}
