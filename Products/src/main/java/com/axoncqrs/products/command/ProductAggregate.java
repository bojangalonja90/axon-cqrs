package com.axoncqrs.products.command;

import com.axoncqrs.corelib.commands.CancelProductReservationCommand;
import com.axoncqrs.corelib.events.ProductReservationCancelledEvent;
import com.axoncqrs.corelib.events.ProductReservedEvent;
import com.axoncqrs.products.ProductsApplication;
import com.axoncqrs.corelib.commands.ReseveProductCommand;
import com.axoncqrs.products.core.events.ProductCreatedEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;

@Aggregate
public class ProductAggregate {

    Logger LOG = LoggerFactory.getLogger(ProductAggregate.class);

    @AggregateIdentifier
    private String id;
    private String title;
    private BigDecimal price;
    private Integer quantity;

    public ProductAggregate() {
    }

    @CommandHandler
    public ProductAggregate(CreateProductCommand createProductCommand) throws Exception {
        LOG.info("*** ProductAggregate CommandHandler");
        ProductCreatedEvent productCreatedEvent = new ProductCreatedEvent();
        BeanUtils.copyProperties(createProductCommand, productCreatedEvent);

        AggregateLifecycle.apply(productCreatedEvent);
    }

    @CommandHandler
    public void handle(ReseveProductCommand reserveProductCommand) {

        if (quantity < reserveProductCommand.getQuantity()) {
            throw new IllegalStateException("Insufficient number of items in stock");
        }

        ProductReservedEvent productReservedEvent = ProductReservedEvent.builder()
                .orderId(reserveProductCommand.getOrderId())
                .productId(reserveProductCommand.getProductId())
                .quantity(reserveProductCommand.getQuantity())
                .userId(reserveProductCommand.getUserId())
                .build();

        AggregateLifecycle.apply(productReservedEvent);

    }

    @CommandHandler
    public void handle(CancelProductReservationCommand cancelProductReservationCommand) {

        ProductReservationCancelledEvent productReservationCancelledEvent = ProductReservationCancelledEvent.builder()
                .orderId(cancelProductReservationCommand.getOrderId())
                .productId(cancelProductReservationCommand.getProductId())
                .quantity(cancelProductReservationCommand.getQuantity())
                .reason(cancelProductReservationCommand.getReason())
                .build();

        AggregateLifecycle.apply(productReservationCancelledEvent);
    }

    //Avoid business logic, only update state of aggregate
    @EventSourcingHandler
    public void on(ProductCreatedEvent productCreatedEvent) {

        LOG.info("*** Event sourcing for ProductCreatedEvent on");
        this.id = productCreatedEvent.getId();
        this.price = productCreatedEvent.getPrice();
        this.quantity = productCreatedEvent.getQuantity();
        this.title = productCreatedEvent.getTitle();
    }

    @EventSourcingHandler
    public void on(ProductReservedEvent productReservedEvent) {
        LOG.info("*** Event sourcing fo ProductReservedEvent on");
        this.quantity -= productReservedEvent.getQuantity();
    }

    @EventSourcingHandler
    public void on(ProductReservationCancelledEvent productReservationCancelledEvent) {
        LOG.info("Event sourcing for ProductReservationCancelledEvent");
        this.quantity += productReservationCancelledEvent.getQuantity();
    }
}
