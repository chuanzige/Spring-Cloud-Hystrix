# Spring-Cloud-Hystrix
一、断路器简介

A service failure in the lower level of services can cause cascading failure all the way up to the user. When calls to a particular service is greater than circuitBreaker.requestVolumeThreshold (default: 20 requests) and failue percentage is greater than circuitBreaker.errorThresholdPercentage (default: >50%) in a rolling window defined by metrics.rollingStats.timeInMilliseconds (default: 10 seconds), the circuit opens and the call is not made. In cases of error and an open circuit a fallback can be provided by the developer.

官网的意思大概就是在进行远程调用的时候如果出现网络问题或服务器宕机或其他情况下回滚一个本地的方法给你或者重定向到别的地方致使调用方也无响应的状态，触发条件默认是5秒20次失败 。看下图,有的时候我们一个方法会调用多个服务，假如ServiceA宕机的情况下，断路器打开fallback一个固定的方法。



二、让我们一起来实践操作下吧😁

(本章测试大概内容:启动两个服务提供者，一个消费者，关闭其中一个服务提供者，查看断路器是否生效；查看hystrix控制面板)

1、首先 我们复用一下上一篇文章的三个服务：eureka-server、eureka-provider、eureka-consumer-feign。我们就模仿一下上图的操作。一个方法调用多个服务。

2、eureka-server不做任何操作，eureka-provider复制出两个项目出来，分别命名为eureka-provider-service1、eureka-provider-service2，同时也复制出一个eureka-consumer-feign出来，命名为eureka-consumer-feign-hystrix

3、我们先对eureka-provider-service1、eureka-provider-service2进行改造,application.properties的实例名称分别改为

eureka-provider-service1、eureka-provider-service2
controller层为了区分这两个服务稍微做了些改变 代码如下

 

package com.example.eurekaprovider.controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
/**
 * @author: MrWang
 * @date: 2018/9/6
 */@RestControllerpublic class StudentController {   
       @GetMapping("/account")    
       public Object student() throws InterruptedException {
        Account student = new Account();
        student.setId("1");
        student.setName("MrWang");
        student.setSex("男");
        student.setPort(8003);        
       return student;
    }    
       class Account{        
       private String id;        
       private String name;        
       private String sex;        
       private Integer port;  
       //省略getset方法
    }     
       
}


package com.example.eurekaprovider.controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
/**
 * @author: MrWang
 * @date: 2018/9/6
 */

@RestController
public class OrderController {    
   @GetMapping("/order")    
    public Object student(){‍        
         Order order = new Order();
         order.setOrderName("订单名称");
         order.setOrderNumber("订单号");
         order.setOrderQuantity("2");        
         return order;‍
    }    
    class Order{        
        private String orderName;        
        private String orderNumber;        
        private String orderQuantity;
        //省略getset方法
    }
}
这里分了区分服务，我们创建了一个账户服务和订单服务，接下来我们来改进服务提供者

首先是接口层，我们把服务提供者的两个bean对象在这边冗余一遍，

 

package com.example.eurekaconsumer.service;
import com.example.eurekaconsumer.bean.Account;
import com.example.eurekaconsumer.bean.Order;
import com.example.eurekaconsumer.service.impl.AccountServiceImpl;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
/**
 * @author: MrWang
 * @date: 2018/9/6
 */

@FeignClient(value = "eureka-provider",fallback = AccountServiceImpl.class)//配置服务提供者实例名称

public interface AccountService {    
   @GetMapping("/account")//服务提供者路由
   Account account();    
}
我们在FeignClient注解后边直接加上fallback指定对应的类就可以，Feign默认是关闭断路器的，我们要在配置文件上开启

feign.hystrix.enabled=true
看一下我们的实现类

package com.example.eurekaconsumer.service.impl;
import com.example.eurekaconsumer.bean.Account;
import com.example.eurekaconsumer.bean.Order;
import com.example.eurekaconsumer.service.AccountService;
import org.springframework.stereotype.Component;
/**
 * @author: MrWang
 * @date: 2018/9/10
 */

@Component
public class AccountServiceImpl implements AccountService {    
    @Override
    public Account account() {
        Account account = new Account();
        account.setId("我是断路器的id");
        account.setName("我是断路器的名称");
        account.setPort(10101);
        account.setSex("断路器性别");       
        return account;
    }    
}
order服务和实现层和上边一样，此处省略

我们在这模拟了一些数据，用来测试断路后的效果。

下边是controller





package com.example.eurekaconsumer.controller;
import com.example.eurekaconsumer.bean.Account;
import com.example.eurekaconsumer.bean.Order;
import com.example.eurekaconsumer.service.AccountService;
import com.example.eurekaconsumer.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;import java.util.Map;
/**
 * @author: MrWang
 * @date: 2018/9/10
 */

@RestController
public class DataController {    
    @Autowired
    private AccountService accountService;    
    @Autowired
    private OrderService orderService;
        @GetMapping("/get")    
        public Map<String,Object> get(){
        Account account = accountService.account();
        Order order = orderService.order();
        Map record = new HashMap();
        record.put("order",order);
        record.put("account",account);        
        return record;
    }
}
好，代码写到这里我们来捋一捋我们的初心

提供两个不同的服务，一个消费者调用这两个服务，关闭其中一个服务，查看断路器的效果。

老样子，我们把改造完的项目打包。



好，依次启动他们



已经启动成功，这里想做负载均衡的小伙伴可以复制出项目出来，实例名称不变，端口号改一下就可以了。这里就不演示了。

我们打开浏览器访问消费者127.0.0.1:8101/get

可以看到我们的数据都是从消费者里取出来的



接下来，我们停掉用户服务，刷新页面



可以看到，我们用户的数据已经接入的是断路器里的数据了，那我们继续停掉订单服务继续看。

不出所料，订单也进入断路器了。



那我们重启这两个服务呢，试试看，



嗯，所有数据又正常显示了。这就是断路器，当调用的远程接口因为不可抗力原因不能访问的情况断路器开启。

三、Hystrix仪表盘

操作了这么多，我们来看看它自带的控制面板吧。

还是在eureka-consumer-feign-hystrix这个项目上操作

1、首先在pom.xml文件添加相关依赖

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-hystrix-dashboard</artifactId>
</dependency>
2、在应用主类上开启监控@EnableHystrixDashboard

package com.example.eurekaconsumer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
@EnableFeignClients
@EnableDiscoveryClient
@EnableHystrixDashboard
@SpringBootApplication
public class EurekaConsumerApplication 
{    public static void main(String[] args) {
        SpringApplication.run(EurekaConsumerApplication.class, args);
    }
}
3、注册Bean

注册HystrixMetricsStreamServlet
    @Bean
    public ServletRegistrationBean getServlet() {
        HystrixMetricsStreamServlet streamServlet = new HystrixMetricsStreamServlet();
        ServletRegistrationBean registrationBean = new ServletRegistrationBean(streamServlet);
        registrationBean.setLoadOnStartup(1);
        registrationBean.addUrlMappings("/hystrix.stream");
        registrationBean.setName("HystrixMetricsStreamServlet");        return registrationBean;
    }

4、访问http://localhost:8101/hystrix

在输入框内输入http://localhost:8101/hystrix.stream



点击Monitor Stream进入监控



我们现在访问htpp://127.0.0.1:8101/get

我请求了三次这个方法，可见三次通过。

我现在停止掉account服务再次请求。



看到有什么不同了没😁

本次的SpringCloudHystrix断路器就介绍到这儿，有什么不会的问题欢迎一起讨论！



