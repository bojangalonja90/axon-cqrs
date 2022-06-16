package com.axoncqrs.orders.orders.core.events;

import com.axoncqrs.orders.orders.command.OrderStatus;
import lombok.Value;

@Value
public class OrderApprovedEvent {
    private final String orderId;
    private final OrderStatus orderStatus = OrderStatus.APPROVED;
}
