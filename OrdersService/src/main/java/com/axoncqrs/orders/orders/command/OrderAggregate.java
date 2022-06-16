package com.axoncqrs.orders.orders.command;

import com.axoncqrs.orders.orders.core.events.OrderApprovedEvent;
import com.axoncqrs.orders.orders.core.events.OrderCreatedEvent;
import com.axoncqrs.orders.orders.core.events.OrderRejectedEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

@Aggregate
public class OrderAggregate {

    @AggregateIdentifier
    private String orderId;
    private String userId;
    private String productId;
    private int quantity;
    private String addressId;
    private OrderStatus orderStatus;

    public OrderAggregate() {
    }

    @CommandHandler
    public OrderAggregate(CreateOrderCommand createOrderCommand) {

        OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent();
        BeanUtils.copyProperties(createOrderCommand, orderCreatedEvent);

        AggregateLifecycle.apply(orderCreatedEvent);

    }

    @CommandHandler
    public void handle(ApproveOrderCommand approveOrderCommand) {
        // publish order approve event
        OrderApprovedEvent orderApprovedEvent = new OrderApprovedEvent(approveOrderCommand.getOrderId());

        AggregateLifecycle.apply(orderApprovedEvent);
    }

    @CommandHandler
    public void on(RejectOrderCommand rejectOrderCommand) {
        OrderRejectedEvent orderRejectedEvent = new OrderRejectedEvent(rejectOrderCommand.getOrderId(), rejectOrderCommand.getReason());

        AggregateLifecycle.apply(orderRejectedEvent);
    }

    //Avoid business logic, only update state of aggregate
    @EventSourcingHandler
    public void on(OrderCreatedEvent event) {
        this.orderId = event.getOrderId();
        this.userId = event.getUserId();
        this.productId = event.getProductId();
        this.quantity = event.getQuantity();
        this.addressId = event.getAddressId();
        this.orderStatus = event.getOrderStatus();
    }

    @EventSourcingHandler
    public void on(OrderApprovedEvent orderApprovedEvent) {
        this.orderStatus = orderApprovedEvent.getOrderStatus();
    }

    @EventSourcingHandler
    public void on(OrderRejectedEvent orderRejectedEvent) {
        this.orderStatus = orderRejectedEvent.getOrderStatus();
    }
}
