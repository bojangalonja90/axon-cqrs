package com.axoncqrs.orders.orders.core.events;

import com.axoncqrs.orders.orders.command.OrderStatus;
import lombok.Value;

@Value
public class OrderRejectedEvent {
    private final String orderId;
    private final String reason;
    private final OrderStatus orderStatus = OrderStatus.REJECTED;
}
