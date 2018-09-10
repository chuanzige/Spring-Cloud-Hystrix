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
