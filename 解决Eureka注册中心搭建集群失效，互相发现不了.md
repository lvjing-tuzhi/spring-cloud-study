# 解决Eureka注册中心搭建集群失效，互相发现不了

> 1.配置文件application.properties/application.ym出错，或者多打空格...
>
> 2.defaultZone配置的路由名字相同，也会导致只能发现一个或者一个都发现不了，所以defaultZone配置的路由需要全部不一样。

## 1、正确使用

1. 第一个注册中心application.properties配置

   ```properties
   server.port=7001
   #Eureka配置
   #主机名
   eureka.instance.hostname=eureka7001.com
   #表示是否向Eureka注册中心注册自己
   eureka.client.register-with-eureka=false
   #fetch-registry如果为false则表示自己为注册中心
   eureka.client.fetch-registry=false
   #单机配置监控页面
   #eureka.client.service-url.defaultZone=http://${eureka.instance.hostname}:${server.port}/eureka
   #集群配置
   eureka.client.service-url.defaultZone=http://eureka7002.com:7002/eureka,http://eureka7003.com:7003/eureka
   ```

2. 第二个注册中心application.properties配置

   ```properties
   server.port=7002
   #Eureka配置
   #主机名
   eureka.instance.hostname=eureka7002.com
   #表示是否向Eureka注册中心注册自己
   eureka.client.register-with-eureka=false
   #fetch-registry如果为false则表示自己为注册中心
   eureka.client.fetch-registry=false
   #单机配置监控页面
   #eureka.client.service-url.defaultZone=http://${eureka.instance.hostname}:${server.port}/eureka
   #集群配置
   eureka.client.service-url.defaultZone=http://eureka7001.com:7001/eureka,http://eureka7003.com:7003/eureka
   ```

3. 第三个注册中心application.properties配置

   ```properties
   server.port=7003
   #Eureka配置
   #主机名
   eureka.instance.hostname=eureka7003.com
   #表示是否向Eureka注册中心注册自己
   eureka.client.register-with-eureka=false
   #fetch-registry如果为false则表示自己为注册中心
   eureka.client.fetch-registry=false
   #单机配置监控页面
   #eureka.client.service-url.defaultZone=http://${eureka.instance.hostname}:${server.port}/eureka
   #集群配置
   eureka.client.service-url.defaultZone=http://eureka7002.com:7002/eureka,http://eureka7001.com:7001/eureka
   ```

4. 生产者配置（提供服务接口的微服务）

   ```properties
   server.port=8001
   spring.application.name=springcloud-provide-dept
   spring.datasource.url=jdbc:mysql://localhost:3306/db01?serverTimezone=Asia/Shanghai&amp&useSSL=true&amp&useUnicode=true&amp&characterEncoding=utf-8
   spring.datasource.username=root
   spring.datasource.password=root
   spring.datasource.driver-class-name=org.gjt.mm.mysql.Driver
   spring.datasource.type=com.alibaba.druid.pool.DruidDataSource
   mybatis.type-aliases-package=com.tuzhi.springcloud.pojo
   mybatis.mapper-locations=classpath:mybatis/mapper/*.xml
   
   #eureka配置，服务要注册到哪里单机
   #eureka.client.service-url.defaultZone=http://localhost:7001/eureka
   #集群配置
   eureka.client.service-url.defaultZone=http://eureka7001.com:7001/eureka,http://eureka7002.com:7002/eureka,http://eureka7003.com:7003/eureka
   eureka.instance.instance-id=provide-dept-8001
   
   #Eureka配置该微服务的信息
   info.app.name=lvjing provide-dept-8001
   info.company=tuzhi
   ```



