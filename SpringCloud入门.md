> 项目地址：https://gitee.com/tuzhilv/spring-cloud-study

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

# 4、Feign

## 1、Feign是什么

1. feign是声明式的web service客户端，它让微服务之间的调用变得更简单了，类似controller调用service。SpringCloud集成了Ribbon和Eureka，可在使用Feign时提供负载均衡的http客户端。
2. 只需要创建一个接口，添加注释即可，这个接口去注册中心拿数据。
3. 调用微服务访问的两种方法：
   * 微服务名字（Ribbon）
   * 接口和注解（Feign）

## 2、Feign能干什么

1. Feign旨在使编写Java Http客户端变得更容易。
2. 前面在使用Ribbon+RestTemplate时，利用RestTemplate对Http请求的封装处理，形成了一套模块化的调用方法。但是在实际开发中，由于对服务依赖的调用肯呢个不止一处，往往一个接口会被多处调用，所以，Feign在此基础上做了进一步封装，由他来帮助我们定义和实现依赖服务接口的定义，在Feign的实现下，我们只需要创建一个接口并使用注解的方式来配置它（类似于以前Dao接口上标注Mapper注解，现在是一个微服务接口上面标志一个Feign注解即可。）即可完成对服务提供方的接口绑定，简化了使用Spring Cloud Ribbon时，自动封装服务调用客户端的开发量。
3. Feign还集成了Ribbon，并且通过轮询实现类客户端的负载均衡。

## 3、使用

1. 导入maven依赖

   ```xml
   <dependency>
       <groupId>org.springframework.cloud</groupId>
       <artifactId>spring-cloud-starter-feign</artifactId>
       <version>1.4.6.RELEASE</version>
   </dependency>
   ```

2. 编写接口并且加注解

   > @FeignClient(value = "注册中心的服务名")

   ```java
   @FeignClient(value = "SPRINGCLOUD-PROVIDE-DEPT")
   public interface DeptClientServer {
       @GetMapping("/dept/list")
       List<Dept> queryAll();
       @GetMapping("/dept/get/{id}")
       Dept queryById(@PathVariable("id") long id);
       @PostMapping("/dept/add")
       boolean addDept(Dept dept);
   }
   ```

3. 在启动类上启动Feign

   > 在哪个模块使用，则在哪个模块启动类上加@EnableFeignClients

   ```java
   @SpringBootApplication
   @EnableEurekaClient
   @EnableFeignClients(basePackages = {"com.tuzhi.springcloud"})
   public class DeptConsumer_feign_80 {
       public static void main(String[] args) {
           SpringApplication.run(DeptConsumer_feign_80.class,args);
       }
   }
   ```

4. 使用

   > 1. 使用@Autowired注入接口
   >
   > 2. @AutoWired
   >
   >    private DeptClientServer server;

```java
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
```

# 5、Hystrix

## 1、服务雪崩

1. 多个微服务之间调用的时候，假设微服务A调用微服务B和微服务C，微服务B和微服务C又调用其他的微服务，这就是所谓的“扇出”、如果扇出的链路上某个微服务的调用响应时间过长或者不可用，对微服务A的调用就会占用越来越多的系统资源，进而引起系统的崩溃，所谓的“雪崩效应”。
2. 对于高流量的应用来说，单一的后端依赖可能会导致所有服务器上的所有资源都在几秒中内饱和。比失败更糟糕的是，这些应用程序还可能导致服务之间的延迟增加，备份队列，线程和其他系统资源紧张，导致整个系统发生更多的级联故障，这些都需要对故障和延迟进行隔离和管理，以便单个依赖的失败，不能取消整个应用程序或者系统。

## 2、什么是Hystrix

1. Hystrix是一个用于处理分布式系统的延迟和容错的开源库，在分布式系统里，许多依赖不可避免的会调用失败，比如超时，异常等，Hystrix能够保证在一个依赖出问题的情况下，不会导致整体服务失败，避免级联故障，以提高分布式系统的弹性。
2. “断路器”本身是一种开关装置，当某个服务单元发生故障之后，通过断路器的故障监控（类似熔断保险丝），向调用方返回一个服务预期的，可处理的备选响应（FallBack），而不是长时间的等待或者抛出调用方法无法处理的异常，这样就可以保证服务调用方的线程不会被长时间占用，从而避免了故障在分布式系统中的蔓延，乃至雪崩。

## 3、能干嘛

1. 服务熔断

2. 服务降级

3. 服务限流

4. 接近实时的监控

   。。。。

## 4、使用

1. 导入maven依赖

   ```xml
   <dependency>
       <groupId>org.springframework.cloud</groupId>
       <artifactId>spring-cloud-starter-hystrix</artifactId>
       <version>1.4.6.RELEASE</version>
   </dependency>
   ```

2. 在启动类上开启支持

   > ```java
   > @EnableCircuitBreaker
   > ```

```java
@SpringBootApplication
//开启Eureka支持,服务启动后自动注册
@EnableEurekaClient
//配置的信息，得到具体的微服务信息
@EnableDiscoveryClient
//添加对Hystrix熔断器的支持
@EnableCircuitBreaker
public class DeptProvider_Hystrix_8001 {
    public static void main(String[] args) {
        SpringApplication.run(DeptProvider_Hystrix_8001.class,args);
    }
}
```

3. 使用

   > 使用@HystrixCommand(fallbackMethod = "hystrixGet")注解判定备选方案，服务出问题时回掉哪个方法

```java
    @GetMapping("/get/{id}")
    @HystrixCommand(fallbackMethod = "hystrixGet")
    public Dept get(@PathVariable("id") Long id) {
        Dept dept = deptService.queryById(id);
        if (dept == null) {
            throw new RuntimeException("id不存在");
        }
        return dept;
    }

//    get请求崩的备选方案
    public Dept hystrixGet(@PathVariable("id") Long id) {
        return new Dept().setDeptno(id).setDname("数据库不存在姓名").setDb_source("不存在该数据库");
    }
```

## 5、服务降级

> 当某一时刻高并发时，关闭某一块服务或者转移某一块服务，叫做服务降级

1. 自定以一个服务降级后的回调类，继承FallbackFactory

   ```2java
   @Component
   public class DeptClientServerFallbackFactory implements FallbackFactory {
       @Override
       public Object create(Throwable throwable) {
           return new DeptClientServer() {
               @Override
               public List&lt;Dept&gt; queryAll() {
                   return null;
               }
   
               @Override
               public Dept queryById(long id) {
                   return new Dept().setDname(&quot;服务降级了&quot;);
               }
   
               @Override
               public boolean addDept(Dept dept) {
                   return false;
               }
           };
       }
   }
   ```

2. 在需要设置降级的类上加注解@FeignClient(value = "SPRINGCLOUD-PROVIDE-DEPT",fallbackFactory = DeptClientServerFallbackFactory.class)

   ```java
   @FeignClient(value = "SPRINGCLOUD-PROVIDE-DEPT",fallbackFactory = DeptClientServerFallbackFactory.class)
   public interface DeptClientServer {
       @GetMapping("/dept/list")
       List<Dept> queryAll();
       @GetMapping("/dept/get/{id}")
       Dept queryById(@PathVariable("id") long id);
       @PostMapping("/dept/add")
       boolean addDept(Dept dept);
   }
   ```

## 6、dashboard监控

> 实时监控服务使用情况，健康等

### 1、监控服务

1. 导入maven依赖

   ```xml
   <dependency>
       <groupId>org.springframework.cloud</groupId>
       <artifactId>spring-cloud-starter-hystrix-dashboard</artifactId>
       <version>1.4.6.RELEASE</version>
   </dependency>
   <dependency>
       <groupId>org.springframework.cloud</groupId>
       <artifactId>spring-cloud-starter-hystrix</artifactId>
       <version>1.4.6.RELEASE</version>
   </dependency>
   ```

2. application.properties配置端口号

   ```properties
   server.port=9001
   ```

3. 开启支持

   > ```
   > 在启动类上加注解@EnableHystrixDashboard
   > ```

```java
@SpringBootApplication
//开启dashboard监控
@EnableHystrixDashboard
public class HystrixDashboard_9001 {
    public static void main(String[] args) {
        SpringApplication.run(HystrixDashboard_9001.class,args);
    }
}
```

### 2、加入监控

> 哪个服务要加入到被监控行列，就进行哪个服务的相关配置

1. 导入maven依赖

   ```xml
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-actuator</artifactId>
   </dependency>
   ```

2. 配置

   > ```java
   > //    加入到dashboard监控中
   >     @Bean
   >     public ServletRegistrationBean a() {
   >         ServletRegistrationBean registrationBean = new ServletRegistrationBean(new HystrixMetricsStreamServlet());
   >         registrationBean.addUrlMappings("/actuator/hystrix.stream");
   >         return registrationBean;
   >     }
   > ```

   ```java
   @SpringBootApplication
   //开启Eureka支持,服务启动后自动注册
   @EnableEurekaClient
   //配置的信息，得到具体的微服务信息
   @EnableDiscoveryClient
   //添加对Hystrix熔断器的支持
   @EnableCircuitBreaker
   public class DeptProvider_Hystrix_8001 {
       public static void main(String[] args) {
           SpringApplication.run(DeptProvider_Hystrix_8001.class,args);
       }
   
   //    加入到dashboard监控中
       @Bean
       public ServletRegistrationBean a() {
           ServletRegistrationBean registrationBean = new ServletRegistrationBean(new HystrixMetricsStreamServlet());
           registrationBean.addUrlMappings("/actuator/hystrix.stream");
           return registrationBean;
       }
   }
   ```

# 6、Zuul

## 1、Zuul是什么

1. Zuul包含了对请求的路由和过滤两个最主要的功能，类似vue的路由守卫。
2. 其中路由功能负载将外部请求转发到具体的微服务实例上，是实现外部访问统一入口的基础，而过滤器功能则负责对请求的处理过程进行干预，是实现请求校验，服务聚合等功能的基础。Zuul和Eureka进行整合，将Zuul自身注册为Eureka服务治理下的应用，同时从Eureka中获得其他微服务的消息，也即以后的访问微服务都是通过Zuul跳转后获得。
3. Zuul服务也是注册进Eureka注册中心。

## 2、使用

1. 导入maven依赖

   ```java
   <dependency>
       <groupId>org.springframework.cloud</groupId>
       <artifactId>spring-cloud-starter-zuul</artifactId>
       <version>1.4.6.RELEASE</version>
   </dependency>
   <dependency>
       <groupId>org.springframework.cloud</groupId>
       <artifactId>spring-cloud-starter-eureka</artifactId>
       <version>1.4.6.RELEASE</version>
   </dependency>
   ```

2. 配置

   > application.properties

   ```properties
   #Eureka配置
   eureka.client.service-url.defaultZone=http://eureka7001.com:7001/eureka,http://eureka7002.com:7002/eureka,http://eureka7003.com:7003/eureka
   eureka.instance.instance-id=zuul-9527
   #Eureka配置该微服务的信息
   info.app.name=zuul-9527
   info.company=tuzhi
   #Zuul路由配置
   #使用自定义路由代替原来的路由
   zuul.routes.mydept.service-id=springcloud-provide-dept
   zuul.routes.mydept.path=/mydept/**
   #隐藏路由
   zuul.ignored-services="springcloud-provide-dept"
   ```

# 7、Config-server（远程配置）

> 把配置放在gitee或者github上，远程进行读取配置文件。
>
> 服务端连接get，然后在客户端连接服务端使用，读取。

## 1、服务端

1. 导入maven依赖

   ```xml
   <dependency>
       <groupId>org.springframework.cloud</groupId>
       <artifactId>spring-cloud-config-server</artifactId>
       <version>2.0.5.RELEASE</version>
   </dependency>
   ```

2. 配置

   > application.yml

```yml
server:
  port: 3344
spring:
  application:
    name: springcloud-config-server
  cloud:
    config:
      server:
        git:
#          git仓库地址
          uri: https://gitee.com/tuzhilv/springcloud-config-server.git
```

3. 开启支持

   > 在启动类上加注解@EnableConfigServer

```java
@SpringBootApplication
@EnableConfigServer
public class ConfigServer_3344 {
    public static void main(String[] args) {
        SpringApplication.run(ConfigServer_3344.class,args);
    }
}
```

4. 测试

   路由访问http://127.0.0.1:3344/application-dev.yml

## 2、使用（客户端）

1. 导入maven依赖

   ```xml
   <dependency>
       <groupId>org.springframework.cloud</groupId>
       <artifactId>spring-cloud-starter-config</artifactId>
       <version>2.0.5.RELEASE</version>
   </dependency>
   ```

2. 创建bootstrap.yml配置文件

   ```yml
   #bootstrap是系统级别的配置，application是用户级别的配置
   spring:
     cloud:
       config:
   #      需要从资源上读取的资源名，不需要后缀名
         name: config-client
         profile: dev
   #      分支
         label: master
   #      服务端地址
         uri: http://127.0.0.1:3344
   ```

3. 测试

   ```java
   @RestController
   public class Test {
   
   //    读取配置文件的数据
       @Value("${spring.application.name}")
       String port;
       @Value("${eureka.client.service-url.defaultZone}")
       String eureka;
   
       @GetMapping("/config")
       public String config() {
           return "port: "+port+" eureka"+eureka;
       }
   }
   ```