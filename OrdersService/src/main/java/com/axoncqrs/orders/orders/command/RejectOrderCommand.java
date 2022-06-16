package com.axoncqrs.orders.orders.command;

import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
public class RejectOrderCommand {

    @TargetAggregateIdentifier
    private final String orderId;
    private final String reason;
}
