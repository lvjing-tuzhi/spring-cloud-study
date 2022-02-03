package com.tuzhi.springcloud.controller;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.ribbon.proxy.annotation.Hystrix;
import com.tuzhi.springcloud.pojo.Dept;
import com.tuzhi.springcloud.service.DeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @program: springcloud
 * @description:
 * @author: 兔子
 * @create: 2022-01-19 21:32
 **/

@RestController
@RequestMapping("/dept")
public class DeptController {
    @Autowired
    private DeptService deptService;
//    获得一些配置的信息，得到具体的微服务信息
    @Autowired
    private DiscoveryClient client;

    @GetMapping("/list")
    public List<Dept> queryAll() {
        return deptService.queryAll();
    }

    @GetMapping("/get/{id}")
    @HystrixCommand(fallbackMethod = "hystrixGet")
    public Dept get(@PathVariable("id") Long id) {
        Dept dept = deptService.queryById(id);
        if (dept == null) {
            throw new RuntimeException("id不存在");
        }
        return dept;
    }

//    get请求崩的备选方案
    public Dept hystrixGet(@PathVariable("id") Long id) {
        return new Dept().setDeptno(id).setDname("数据库不存在姓名").setDb_source("不存在该数据库");
    }

    @PostMapping("/add")
    public boolean addDept(Dept dept) {
        return deptService.addDept(dept);
    }

//    查看当前微服务在注册中心点信息
    @GetMapping("/discovery")
    public Object discovery() {
        List<String> services = client.getServices();
        System.out.println("discovery======>" + services);
        List<ServiceInstance> instances = client.getInstances("SPRINGCLOUD-PROVIDE-DEPT");
        System.out.println("instace" + instances);
        for (ServiceInstance instance : instances) {
            System.err.println(instance.getInstanceId());
            System.err.println(instance.getHost());
            System.err.println(instance.getScheme());
            System.err.println(instance.getUri());
        }
        return services;
    }
}
