package org.learn.security_redis_login.config;

import org.learn.security_redis_login.common.redis.RedisDao;
import org.learn.security_redis_login.common.sercurity.filter.TokenAuthFilter;
import org.learn.security_redis_login.common.sercurity.filter.TokenLoginFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author Skyrimal
 * @version 1.0
 * @date 2021/8/19 0019 11:15
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private RedisDao redisDao;
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilter(new TokenLoginFilter(redisDao,this.authenticationManager())).addFilter(new TokenAuthFilter(this.authenticationManager(),redisDao));

        http.formLogin().permitAll().and().authorizeRequests().antMatchers("/","/login/**").permitAll().anyRequest().authenticated();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
