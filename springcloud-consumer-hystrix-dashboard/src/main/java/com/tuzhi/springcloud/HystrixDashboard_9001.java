package com.tuzhi.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;

/**
* @program: SpringCloud
*
* @description:
*
* @author: 兔子
*
* @create: 2022-02-03 20:49
**/

@SpringBootApplication
//开启dashboard监控
@EnableHystrixDashboard
public class HystrixDashboard_9001 {
    public static void main(String[] args) {
        SpringApplication.run(HystrixDashboard_9001.class,args);
    }
}
