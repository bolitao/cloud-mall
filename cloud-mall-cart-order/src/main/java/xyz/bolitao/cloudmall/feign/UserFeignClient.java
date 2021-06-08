package xyz.bolitao.cloudmall.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import xyz.bolitao.cloudmall.user.model.entity.User;

/**
 * userFeignClient.getUser() 不用判空是因为已经有 zuul filter 的前置拦截
 *
 * @author boli.tao
 */
@FeignClient(contextId = "cloud-mall-user-client", value = "cloud-mall-user")
public interface UserFeignClient {
    /**
     * 通过 feign 的远程调用默认不带 session 等相关信息
     *
     * @return current user
     */
    @GetMapping("/getUser")
    User getUser();
}
