package com.example.eurekaconsumer.service;

import com.example.eurekaconsumer.bean.Account;
import com.example.eurekaconsumer.service.impl.AccountServiceImpl;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author: MrWang
 * @date: 2018/9/6
 */

@FeignClient(value = "eureka-provider-service1",fallback = AccountServiceImpl.class)//配置服务提供者实例名称
public interface AccountService {

    @GetMapping("/account")//服务提供者路由
    Account account();

}
