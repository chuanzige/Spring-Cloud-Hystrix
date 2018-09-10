package com.example.eurekaconsumer.service;

import com.example.eurekaconsumer.bean.Order;
import com.example.eurekaconsumer.service.impl.OrderServiceImpl;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author: MrWang
 * @date: 2018/9/10
 */
@FeignClient(value = "eureka-provider-service2",fallback = OrderServiceImpl.class)//配置服务提供者实例名称
public interface OrderService {

    @GetMapping("/order")//服务提供者路由
    Order order();
}
