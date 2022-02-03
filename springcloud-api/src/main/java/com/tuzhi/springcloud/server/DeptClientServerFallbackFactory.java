package com.tuzhi.springcloud.server;

import com.tuzhi.springcloud.pojo.Dept;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @program: SpringCloud
 * @description:
 * @author: 兔子
 * @create: 2022-02-03 20:01
 **/

@Component
public class DeptClientServerFallbackFactory implements FallbackFactory {
    @Override
    public Object create(Throwable throwable) {
        return new DeptClientServer() {
            @Override
            public List<Dept> queryAll() {
                return null;
            }

            @Override
            public Dept queryById(long id) {
                return new Dept().setDname("服务降级了");
            }

            @Override
            public boolean addDept(Dept dept) {
                return false;
            }
        };
    }
}
