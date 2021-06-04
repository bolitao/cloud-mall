package xyz.bolitao.cloudmall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@SpringBootApplication
@EnableRedisHttpSession
@EnableFeignClients
@MapperScan(basePackages = {"xyz.bolitao.cloudmall.category.model.dao", "xyz.bolitao.cloudmall.product.model.dao"})
@EnableCaching
public class CategoryProductApp {
    public static void main(String[] args) {
        SpringApplication.run(CategoryProductApp.class, args);
    }
}
