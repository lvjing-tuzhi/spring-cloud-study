package com.tuzhi.springcloud.service;

import com.tuzhi.springcloud.pojo.Dept;

import java.util.List;

/**
 * @program: springcloud
 * @description:
 * @author: 兔子
 * @create: 2022-01-19 21:30
 **/

public interface DeptService {
    List<Dept> queryAll();
    Dept queryById(long id);
    boolean addDept(Dept dept);
}
