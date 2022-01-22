package com.tuzhi.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @program: SpringCloud
 * @description:
 * @author: 兔子
 * @create: 2022-01-22 14:18
 **/

@SpringBootApplication
@EnableEurekaServer
public class EurekaServer_7003 {
    public static void main(String[] args) {
        SpringApplication.run(EurekaServer_7003.class,args);
    }
}
