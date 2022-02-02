package com.tuzhi.springcloud;

import com.tuzhi.myrule.LvRandomRule;
import com.tuzhi.myrule.LvRule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.ribbon.RibbonClient;

/**
 * @program: springcloud
 * @description:
 * @author: 兔子
 * @create: 2022-01-21 14:56
 **/

@SpringBootApplication
@EnableEurekaClient
@RibbonClient(name = "lv", configuration = LvRule.class)
public class DeptConsumer_80 {
    public static void main(String[] args) {
        SpringApplication.run(DeptConsumer_80.class,args);
    }
}
