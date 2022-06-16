package com.axoncqrs.orders.orders.query;

import com.axoncqrs.orders.orders.core.data.OrderEntity;
import com.axoncqrs.orders.orders.core.data.OrdersRepository;
import com.axoncqrs.orders.orders.core.events.OrderApprovedEvent;
import com.axoncqrs.orders.orders.core.events.OrderCreatedEvent;
import com.axoncqrs.orders.orders.core.events.OrderRejectedEvent;
import org.axonframework.eventhandling.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class OrderEventHandler {

    private static final Logger LOG = LoggerFactory.getLogger(OrderEventHandler.class);

    private final OrdersRepository ordersRepository;

    public OrderEventHandler(OrdersRepository ordersRepository) {
        this.ordersRepository = ordersRepository;
    }

    @EventHandler
    public void on(OrderCreatedEvent event) {

        if (ordersRepository.existsById(event.getOrderId())) {
            throw new IllegalArgumentException("Order exists " + event.getOrderId());
        }
        OrderEntity orderEntity = new OrderEntity();

        BeanUtils.copyProperties(event, orderEntity);
        ordersRepository.save(orderEntity);

    }

    @EventHandler
    public void on(OrderApprovedEvent event) {
        LOG.info("On OrderApprovedEvent");

        OrderEntity orderEntity =
                ordersRepository.findByOrderId(event.getOrderId())
                        .orElseThrow(() -> new IllegalArgumentException("No Order exists " + event.getOrderId()));

        orderEntity.setOrderStatus(event.getOrderStatus());
        ordersRepository.save(orderEntity);
    }

    @EventHandler
    public void on(OrderRejectedEvent event) {
        LOG.info("On OrderRejectedEvent");

        OrderEntity orderEntity =
                ordersRepository.findByOrderId(event.getOrderId())
                        .orElseThrow(() -> new IllegalArgumentException("No Order exists " + event.getOrderId()));

        orderEntity.setOrderStatus(event.getOrderStatus());
        ordersRepository.save(orderEntity);
    }

}
