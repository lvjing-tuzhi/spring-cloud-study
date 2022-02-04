package com.tuzhi.springcloud.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: SpringCloud
 * @description:
 * @author: 兔子
 * @create: 2022-02-04 21:46
 **/

@RestController
public class Test {

//    读取配置文件的数据
    @Value("${spring.application.name}")
    String port;
    @Value("${eureka.client.service-url.defaultZone}")
    String eureka;

    @GetMapping("/config")
    public String config() {
        return "port: "+port+" eureka"+eureka;
    }
}
