package com.tuzhi.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/**
 * @program: SpringCloud
 * @description:
 * @author: 兔子
 * @create: 2022-02-04 15:40
 **/

@SpringBootApplication
//开启Zuul支持
@EnableZuulProxy
public class Zuul_9527 {
    public static void main(String[] args) {
        SpringApplication.run(Zuul_9527.class,args);
    }
}
