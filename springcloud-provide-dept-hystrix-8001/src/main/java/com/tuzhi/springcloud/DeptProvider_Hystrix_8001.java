package com.tuzhi.springcloud;

import com.netflix.hystrix.contrib.metrics.eventstream.HystrixMetricsStreamServlet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;

/**
 * @program: springcloud
 * @description:
 * @author: 兔子
 * @create: 2022-01-19 21:47
 **/

@SpringBootApplication
//开启Eureka支持,服务启动后自动注册
@EnableEurekaClient
//配置的信息，得到具体的微服务信息
@EnableDiscoveryClient
//添加对Hystrix熔断器的支持
@EnableCircuitBreaker
public class DeptProvider_Hystrix_8001 {
    public static void main(String[] args) {
        SpringApplication.run(DeptProvider_Hystrix_8001.class,args);
    }

//    加入到dashboard监控中
    @Bean
    public ServletRegistrationBean a() {
        ServletRegistrationBean registrationBean = new ServletRegistrationBean(new HystrixMetricsStreamServlet());
        registrationBean.addUrlMappings("/actuator/hystrix.stream");
        return registrationBean;
    }
}
