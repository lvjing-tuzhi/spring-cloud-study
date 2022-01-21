package com.tuzhi.springcloud.service;

import com.tuzhi.springcloud.dao.DeptDao;
import com.tuzhi.springcloud.pojo.Dept;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @program: springcloud
 * @description:
 * @author: 兔子
 * @create: 2022-01-19 21:30
 **/

@Service
public class DeptServiceImpl implements DeptService{

    @Autowired
    private DeptDao deptDao;

    @Override
    public List<Dept> queryAll() {
        return deptDao.queryAll();
    }

    @Override
    public Dept queryById(long id) {
        return deptDao.queryById(id);
    }

    @Override
    public boolean addDept(Dept dept) {
        return deptDao.addDept(dept);
    }
}
