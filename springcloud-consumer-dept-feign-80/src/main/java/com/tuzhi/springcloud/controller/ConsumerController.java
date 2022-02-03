package com.tuzhi.springcloud.controller;

import com.tuzhi.springcloud.pojo.Dept;
import com.tuzhi.springcloud.server.DeptClientServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
    private DeptClientServer server;

    @GetMapping("/list")
    public List<Dept> list() {
        return server.queryAll();
    }

    @GetMapping("get/{id}")
    public Dept get(@PathVariable("id") Long id) {
        return server.queryById(id);
    }

    @PostMapping("/add")
    public Boolean add(Dept dept) {
        return server.addDept(dept);
    }

}
