package com.tuzhi.myrule;

import com.netflix.loadbalancer.IRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @program: SpringCloud
 * @description:
 * @author: 兔子
 * @create: 2022-02-02 23:10
 **/

@Configuration
public class LvRule {

    @Bean
    public IRule myRule() {
        return new LvRandomRule();
    }
}
