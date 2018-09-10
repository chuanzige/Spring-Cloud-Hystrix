package com.example.eurekaconsumer.service.impl;

import com.example.eurekaconsumer.bean.Order;
import com.example.eurekaconsumer.service.OrderService;
import org.springframework.stereotype.Component;

/**
 * @author: MrWang
 * @date: 2018/9/10
 */

@Component
public class OrderServiceImpl implements OrderService {
    @Override
    public Order order() {
        Order order = new Order();
        order.setOrderName("我是断路器订单姓名");
        order.setOrderNumber("我是断路器订单编号");
        order.setOrderQuantity("1231");
        return order;
    }
}
