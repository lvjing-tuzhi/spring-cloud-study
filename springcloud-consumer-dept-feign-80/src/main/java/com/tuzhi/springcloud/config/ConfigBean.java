package com.tuzhi.springcloud.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @program: springcloud
 * @description:
 * @author: 兔子
 * @create: 2022-01-21 14:42
 **/

@Configuration
public class ConfigBean {

    @Bean
    @LoadBalanced //    使用Ribbon实现负责均衡
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
