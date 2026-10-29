package com.kodewala.order.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.kodewala.order.event.OrderCreatedEvent;
import com.kodewala.order.producer.OrderKafkaProducer;
import java.time.LocalDateTime;

import com.kodewala.order.entity.Order;
import com.kodewala.order.repository.OrderRepository;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private OrderRepository orderRepository;
    private OrderKafkaProducer orderKafkaProducer;

    public OrderController(OrderRepository orderRepository,
            OrderKafkaProducer orderKafkaProducer) {

this.orderRepository = orderRepository;
this.orderKafkaProducer = orderKafkaProducer;

}
    @PostMapping
    public Order createOrder(@RequestBody Order order) {

        Order savedOrder = orderRepository.save(order);

        OrderCreatedEvent event = new OrderCreatedEvent();

        event.setEventId("EVT-" + savedOrder.getOrderId());
        event.setEventType("ORDER_CREATED");
        event.setOrderId(savedOrder.getOrderId());
        event.setCustomerId(savedOrder.getCustomerId());
        event.setAmount(savedOrder.getAmount());
        event.setDeliveryAddress(savedOrder.getDeliveryAddress());
        event.setEventTime(LocalDateTime.now());

        orderKafkaProducer.publishOrderCreated(event);

        return savedOrder;
    }
}