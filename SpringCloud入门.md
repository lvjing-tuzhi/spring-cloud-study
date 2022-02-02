# 1、Rest方式

## 1、服务提供者

> 正常的controller的提供url接口

## 2、服务消费者（使用）

### 1、配置config，注入RestTemplate

1. config>ConfigBean.java

   ```java
   @Configuration
   public class ConfigBean {
   
       @Bean
       public RestTemplate restTemplate() {
           return new RestTemplate();
       }
   }
   ```

### 2、使用

> 同过restTemplate里面的xxxForObject方法获取其他url的api

```java
@RestController
@RequestMapping("/consumer")
public class ConsumerController {

    @Autowired
    RestTemplate restTemplate;
    public final static String URL_PRE = "http://127.0.0.1:8001/dept";

    @GetMapping("/list")
    public List<Dept> list() {
        return restTemplate.getForObject(URL_PRE+"/list",List.class);
    }

    @GetMapping("get/{id}")
    public Dept get(@PathVariable("id") Long id) {
        return restTemplate.getForObject(URL_PRE+"/get/"+id,Dept.class);
    }

    @PostMapping("/add")
    public Boolean add(Dept dept) {
        return restTemplate.postForObject(URL_PRE+"/add",dept,Boolean.class);
    }

}
```

# 2、Eureka（服务注册与发现）

## 1、什么是Eureka

1. Eureka是Netflix的一个子模块，也是核心模块之一。Eureka是一个基于REST的服务，用于定位服务，以实现云端中间层服务发现和故障转移，服务注册与发现对于微服务来说是非常重要的，有了服务发现与注册，只需要使用服务的标识符，就可以访问到服务，而不需要修改服务调用的配置文件了，功能类似Dubbo的注册中心，比如Zookeeper。

## 2、基本架构

1. SpringCloud封装了NetFlix公司开发的Eureka模块来实现服务注册与发现。
2. Eureka采用了C-S的架构设计，EurekaServer作为服务注册功能的服务器，他是服务注册中心。
3. 而系统中的其他微服务。使用Eureka的客户端连接到EurekaServce并维持心疼连接。这样系统的维护人员就可以通过EurekaServece来监控系统中各个微服务是否正常运行，SpringCloud的一些其他模块(比如Zuul)就可以通过EurekaServer来发现系统中的其他微服务，并执行相关的逻辑。
4. Eureka包含两个组件：Eureka Service和Eureka Client。
5. Eureka Server提供服务注册，各个节点启动后，会再EurekaServe中进行注册，这样EurekaServer中的服务注册表中将会出现所有可用服务节点的信息，服务节点的信息可以在界面中直观的看到。
6. Eureka Client是一个Java客户端，用于简化EurekaServer的交互，客户端同时也具备一个内置的，使用轮询负载算法的负载均衡器。在应用启动后，将会向EurekaServer发送心跳（默认周期为30秒）。如果EurekaServer在多个心跳周期内没有收到某个节点的心跳，EurekaServer将会从服务注册表中把这个服务节点移除掉（默认周期为90秒）。
7. 三大角色
   * Eureka Server：提供服务的注册与发现。
   * Service Provider：将自身服务注册到Eureka中获取注册服务列表，从而找到消费服务。

## 3、自我保护机制

1. 某时刻某一个微服务不可用了，eureka不会立刻清理，而是会对该微服务的信息进行保存。
2. 默认情况下，如果EurekaServer在一定时间内没有接收到某个微服务实例的心跳，EurekaServe将会注销该实例（默认90秒）。但是当网络分区发生故障时，微服务与Eureka之间无法正常通行，以上行为可能变得非常危险了，因为该微服务本身是健康的，所有此时不应该注销这个服务。Eureka通过自我保护机制来解决这个问题-当EurekaServer节点在短时间内丢失过多客户端时（可能发生了网络分区故障），那么这个节点就会进入自我保护模式。一旦进入该模式，EurekaServer就会保护服务注册表中的信息，不再删除服务注册表中的数据（也就是不会注销任何微服务）。当网络故障恢复后，该Eurekaserver节点会自动退出自我保护模式。
3. 在自我保护模式时，EurekaServer会保护服务注册表中的信息，不再注销任何服务实例。当它收到的心跳数重新恢复到阈值以上时，该EurekaServer节点就会自动退出自我保护模式。它的设计哲学就是宁可保留错误的服务注册信息，也不盲目注销任何可能健康的服务实例。
4. 综上，自我保护模式是一种应对网络异常的安全保护措施。它的架构哲学是宁可同时保留所有微服务（健康的微服务和不健康的微服务都会保留），也不盲目注销任何健康的微服务。使用自我保护模式，可以让Eureka集群更加的健壮和稳定
5. 在SpringCloud中，可以使用eureka.server.enable-self-preservation = false 禁用自我保护模式==不推荐关闭自我保护机制==

## 4、使用

### 1、注册中心

1. maven导入依赖

   ```xml
   <dependency>
       <groupId>org.springframework.cloud</groupId>
       <artifactId>spring-cloud-starter-eureka-server</artifactId>
       <version>1.4.6.RELEASE</version>
   </dependency>
   ```

2. 填写相关配置

   > application.properties

```properties
server.port=7001
#Eureka配置
#主机名
eureka.instance.hostname=localhost
#表示是否向Eureka注册中心注册自己
eureka.client.register-with-eureka=false
#fetch-registry如果为false则表示自己为注册中心
eureka.client.fetch-registry=false
#监控页面
eureka.client.service-url.defaultZone=http://${eureka.instance.hostname}:${server.port}/eureka
```

3. 在启动类上加注解@@EnableEurekaServer

   ```java
   @SpringBootApplication
   @EnableEurekaServer
   public class EurekaServer_7001 {
       public static void main(String[] args) {
           SpringApplication.run(EurekaServer_7001.class,args);
       }
   }
   ```

### 2、把微服务注册到注册中心

1. 导入maven依赖

   ```xml
   <!--        Eureka-使当前的微服务可以注册到Eureka注册中心里面-->
           <dependency>
               <groupId>org.springframework.cloud</groupId>
               <artifactId>spring-cloud-starter-eureka</artifactId>
               <version>1.4.6.RELEASE</version>
           </dependency>
   <!--        Eureka监控信息-->
           <dependency>
               <groupId>org.springframework.boot</groupId>
               <artifactId>spring-boot-starter-actuator</artifactId>
           </dependency>
   ```

2. 配置application.properties

   ```properties
   #eureka配置，服务要注册到哪里
   eureka.client.service-url.defaultZone=http://localhost:7001/eureka
   eureka.instance.instance-id=provide-dept-8001
   
   #Eureka配置该微服务的信息
   info.app.name=lvjing provide-dept-8001
   info.company=tuzhi
   ```

3. 以上步骤完成，当项目启动的时候就会自动注册到注册中心。

4. 查看当前微服务在注册中心点信息

   ```java
   import org.springframework.cloud.client.discovery.DiscoveryClient;
   
   @RestController
   @RequestMapping("/dept")
   public class DeptController {
   //    获得一些配置的信息，得到具体的微服务信息,DiscoveryClient接口
       @Autowired
       private DiscoveryClient client;
   
   
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
   ```

## 5、Eureka注册中心集群搭建

> defaultZone后面配置的url添加多个以逗号分隔就可以了。

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

## 6、Eureka和Zookeeper区别

#### 1、CAP

> RDBMS （Mysql、Oracle、sqlServer）===>ACID
>
> NoSQL（redis、mongdb）===> CAP

- C（Consistency）强一致性
- A（Availability）可用性
- P（Partition tolerance）分区容错性

#### 2、CAP理论

1. 一个分布式系统不可能同时很好的满足一致性，可用性和分区容错性这三个需求。

2. 根据CAP原理，将NoSQL数据库分成了满足CA原则，满足CP原则和满足AP原则三大类：

   - CA：单点集群，满足一致性，可用性的系统，通常可扩展性较差

   - CP：满足一致性，分区容错性的系统，通常性能不是特别高

   - AP：满足可用性，分区容错性的系统，通常可能对一致性要求低一些

3. 著名的CAP理论指出，一个分布式系统不可能同时满足C（一致性）、A（可用性）、P（容错性）。
4. Zookeeper保证的是CP；Eureka保证的是AP；

#### 3、Zookeeper保证的是CP

1. 当向注册中心查询服务列表时，我们可以容忍注册中心返回的是几分钟以前的注册信息，但不能接受服务直接down掉不可用。也就是说，服务注册功能对可用性的要求要高于一致性。但是Zookeeper会出现这样一种情况，当master节点因为网络故障与其他节点失去联系时，剩余节点会重新进行leader选举。而leader的选举时间太长，30-120s，且选举期间整个Zookeeper集群都是不可用的，这就导致在选举期间注册服务瘫痪。在云部署的环境下，因为网络问题使得Zookeeper集群失去master节点是较大概率会发生的事件，虽然服务最终能够恢复，但是漫长的选举时间导致的注册长期不可用是不能容忍的。

#### 4、Eureka保证的是AP

1. Eureka看明白了这一点，因此在设计时就优先保证可用性。Eureka各个节点都是平等的，几个节点挂掉不会影响正常节点的工作，剩余的节点依然可以提供注册和查询服务。而Eureka的客户端在向某个Eureka注册时，如果发现连接失败，则会自动切换至其他节点，只要有一台Eureka还在，就能保住注册服务的可用性，只不过查到的信息可能不是最新的，除此之外，Eureka还有一种自我保护机制，如果Eureka就认为客户端与注册中心出现了网络故障，此时会出现以下几种情况：
   * Eureka不再从注册列表中移除因为长时间没收到心跳而应该过期的服务。
   * Eureka仍然能够接受新服务的注册和查询请求，但是不会被同步到其他节点上去（即保证当前节点依然可用）。
   * 当网络稳定时，当前新的注册信息会被同步到其他节点中。
2. 因此，Eureka可以很好的应对网络故障导致部分节点失去联系的情况，而不会像Zookeeper那样使整个注册服务瘫痪。

# 3、Ribbon

## 1、ribbon是什么

1. Spring Cloud Ribbon是基于Netflix Ribbon实现的一套客户端负载均衡的工具。
2. 简单的来说，Ribbon是NetFlix发布的开源项目，主要功能是提供客户端的软件负载均衡算法，将NetFlix的中间层连接在一起。Ribbon的客户端组件提供一系列完整的配置项如：连接超时、重试等等。简单的说，就是在配置文件中列出LoadBalancer（简称LB：负载均衡）后面所有的机器，Ribbon会自动的帮助你基于某种规则（如简单轮询，随机连接等等）去连接这些机器。我们也很容易使用Ribbon实现自定义的负载均衡算法。

## 2、Ribbon能干嘛

1. LB，即负载均衡（Load Balance），在微服务或分布式集群中经常用到的一种应用。
2. 负载均衡简单的说就是将用户的请求平摊的分配到多个服务上，从而达到系统的HA（高可用）。
3. 常用的负载均衡软件有Nginx，Lvs等等。
4. dubbo、SpringCloud中均给我们提供了负载均衡，SpringCloud的负载均衡算法可以自定义。
5. 负载均衡简单分离：
   * 集中式LB：
     * 即在服务的消费方和提供方之间使用独立的LB设施，如Nginx，由该设施负责把访问请求通过某种策略转发至服务的提供方！
   * 进程式LB：
     * 将LB逻辑集成到消费方，消费方从服务注册中心获知那些地址可用，然后自己再从这些地址中选出一个合适的服务器。
     * Ribbon就属于进程内LB，它只是一个类库，集成于消费方进程，消费方通过它来获取服务提供的地址！

## 3、使用

#### 1、导入maven依赖

```xml
<!-- ribbon -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-ribbon</artifactId>
            <version>1.4.6.RELEASE</version>
        </dependency>
<!--        Eureka客户端-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-eureka</artifactId>
            <version>1.4.6.RELEASE</version>
        </dependency>
```

#### 2、在启动器上加@EnableEurekaClient注解

```java
@SpringBootApplication
@EnableEurekaClient
public class DeptConsumer_80 {
    public static void main(String[] args) {
        SpringApplication.run(DeptConsumer_80.class,args);
    }
}
```

#### 3、编写配置

> application.properties

```properties
#Eureka配置
#不向Eureka注册自己
eureka.client.register-with-eureka=false
eureka.client.service-url.defaultZone=http://eureka7001.com:7001/eureka,http://eureka7002.com:7002/eureka,http://eureka7003.com:7003/eureka
```

#### 4、编写配置类加@LoadBalanced注解

```java
@Configuration
public class ConfigBean {

    @Bean
    @LoadBalanced //    使用Ribbon实现负责均衡
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
```

#### 5、使用

> 1. 使用Ribbon从1Eureka注册中心拿服务，地址是服务名
> 2. public final static String URL_PRE = "http://SPRINGCLOUD-PROVIDE-DEPT/dept";

```java
@RestController
@RequestMapping("/consumer")
public class ConsumerController {

    @Autowired
    RestTemplate restTemplate;
//    public final static String URL_PRE = "http://127.0.0.1:8001/dept";
//    使用Ribbon从Eureka注册中心拿服务，地址是服务名
    public final static String URL_PRE = "http://SPRINGCLOUD-PROVIDE-DEPT/dept";

    @GetMapping("/list")
    public List<Dept> list() {
        return restTemplate.getForObject(URL_PRE+"/list",List.class);
    }

    @GetMapping("get/{id}")
    public Dept get(@PathVariable("id") Long id) {
        return restTemplate.getForObject(URL_PRE+"/get/"+id,Dept.class);
    }

    @PostMapping("/add")
    public Boolean add(Dept dept) {
        return restTemplate.postForObject(URL_PRE+"/add",dept,Boolean.class);
    }

}
```

## 4、自定义Ribbon策略

1. 自定义策略的方法不能于启动类同级，要在启动类的上一级。
2. 在启动类上引用自定义的方法@RibbonClient(name = "custom", configuration = CustomConfiguration.class)







































项目地址：https://gitee.com/tuzhilv/spring-cloud-study