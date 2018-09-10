package com.example.eurekaconsumer.controller;

import com.example.eurekaconsumer.bean.Account;
import com.example.eurekaconsumer.bean.Order;
import com.example.eurekaconsumer.service.AccountService;
import com.example.eurekaconsumer.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

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
