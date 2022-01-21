package com.tuzhi.springcloud.Controller;

import com.tuzhi.springcloud.pojo.Dept;
import com.tuzhi.springcloud.service.DeptService;
import org.springframework.beans.factory.annotation.Autowired;
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
}
