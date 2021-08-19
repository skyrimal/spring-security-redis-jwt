package org.learn.security_redis_login;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("org.learn.security_redis_login.**.mapper")
public class SecurityRedisLoginApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecurityRedisLoginApplication.class, args);
    }

}
