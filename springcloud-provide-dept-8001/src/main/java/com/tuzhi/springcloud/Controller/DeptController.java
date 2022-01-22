package com.tuzhi.springcloud.Controller;

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
    public Dept get(@PathVariable("id") Long id) {
        return deptService.queryById(id);
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
