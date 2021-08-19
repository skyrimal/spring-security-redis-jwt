package org.learn.security_redis_login.test.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Skyrimal
 * @version 1.0
 * @date 2021/8/19 0019 12:17
 */
@RestController
public class BaseController {
    @RequestMapping("hello")
    public String hello(){
        return "hello";
    }
    @RequestMapping("hello1")
    public String hello1(){
        return "hello1";
    }
    @RequestMapping("hello2")
    public String hello2(){
        return "hello2";
    }
    @RequestMapping("hello3")
    public String hello3(){
        return "hello3";
    }
}
