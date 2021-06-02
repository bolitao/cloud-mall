package xyz.bolitao.cloudmall.zuul.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import xyz.bolitao.cloudmall.user.model.entity.User;

@FeignClient(value = "cloud-mall-user")
public interface UserFeignClient {
    @PostMapping("/checkAdminRole")
    Boolean checkAdminRole(@RequestBody User user);
}
