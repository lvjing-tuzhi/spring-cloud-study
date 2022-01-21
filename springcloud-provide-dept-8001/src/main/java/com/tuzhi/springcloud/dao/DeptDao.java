package com.tuzhi.springcloud.dao;

import com.tuzhi.springcloud.pojo.Dept;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @program: springcloud
 * @description: 部门dao层接口
 * @author: 兔子
 * @create: 2022-01-19 21:19
 **/

@Mapper
@Repository
public interface DeptDao {
    List<Dept> queryAll();
    Dept queryById(long id);
    boolean addDept(Dept dept);
}
