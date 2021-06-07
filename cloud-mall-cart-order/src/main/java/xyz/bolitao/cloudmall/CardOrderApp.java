package xyz.bolitao.cloudmall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * @author boli.tao
 */
@SpringBootApplication
@MapperScan(basePackages = {"xyz.bolitao.cloudmall.cart.model.dao"})
@EnableRedisHttpSession
@EnableFeignClients
public class CardOrderApp {
    public static void main(String[] args) {
        SpringApplication.run(CardOrderApp.class, args);
    }
}
