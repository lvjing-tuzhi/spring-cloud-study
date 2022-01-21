package com.tuzhi.springcloud.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @program: springcloud
 * @description: 部门实体类
 * @author: 兔子
 * @create: 2022-01-19 20:55
 **/

@Data
@NoArgsConstructor
//允许链式写法
@Accessors(chain = true)
public class Dept {
    private long deptno;
    private String dname;
    private String db_source;

    public Dept(String dname) {
        this.dname = dname;
    }
}
