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















项目地址：https://gitee.com/tuzhilv/spring-cloud-study