package com.tuzhi.springcloud.controller;

import com.tuzhi.springcloud.pojo.Dept;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * @program: springcloud
 * @description:
 * @author: 兔子
 * @create: 2022-01-21 14:45
 **/

@RestController
@RequestMapping("/consumer")
public class ConsumerController {

    @Autowired
    RestTemplate restTemplate;
//    public final static String URL_PRE = "http://127.0.0.1:8001/dept";
//    使用Ribbon从Eureka注册中心拿服务，地址是服务名
    public final static String URL_PRE = "http://SPRINGCLOUD-PROVIDE-DEPT/dept";

    @GetMapping("/list")
    public List<Dept> list() {
        return restTemplate.getForObject(URL_PRE+"/list",List.class);
    }

    @GetMapping("get/{id}")
    public Dept get(@PathVariable("id") Long id) {
        return restTemplate.getForObject(URL_PRE+"/get/"+id,Dept.class);
    }

    @PostMapping("/add")
    public Boolean add(Dept dept) {
        return restTemplate.postForObject(URL_PRE+"/add",dept,Boolean.class);
    }

}
