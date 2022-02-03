package com.tuzhi.springcloud.server;

import com.tuzhi.springcloud.pojo.Dept;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

/**
 * @program: SpringCloud
 * @description:
 * @author: 兔子
 * @create: 2022-02-03 13:42
 **/

@FeignClient(value = "SPRINGCLOUD-PROVIDE-DEPT",fallbackFactory = DeptClientServerFallbackFactory.class)
public interface DeptClientServer {
    @GetMapping("/dept/list")
    List<Dept> queryAll();
    @GetMapping("/dept/get/{id}")
    Dept queryById(@PathVariable("id") long id);
    @PostMapping("/dept/add")
    boolean addDept(Dept dept);
}
