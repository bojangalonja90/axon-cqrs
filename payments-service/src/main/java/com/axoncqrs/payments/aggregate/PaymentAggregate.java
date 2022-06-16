package com.axoncqrs.payments.aggregate;

import com.axoncqrs.corelib.commands.ProcessPaymentCommand;
import com.axoncqrs.corelib.events.PaymentProcessedEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aggregate
public class PaymentAggregate {

    Logger LOG = LoggerFactory.getLogger(PaymentAggregate.class);

    @AggregateIdentifier
    private String paymentId;
    private String orderId;

    public PaymentAggregate() { }

    @CommandHandler
    public PaymentAggregate(ProcessPaymentCommand processPaymentCommand) {

        LOG.info("Processing payment: " + processPaymentCommand.getPaymentId());

        if (processPaymentCommand == null || processPaymentCommand.getPaymentId() == null || processPaymentCommand.getOrderId() == null) {
            throw new IllegalArgumentException("Sometjhing is null");
        }

        PaymentProcessedEvent paymentProcessedEvent = PaymentProcessedEvent.builder()
                .paymentId(processPaymentCommand.getPaymentId())
                .orderId(processPaymentCommand.getOrderId())
                .build();

        AggregateLifecycle.apply(paymentProcessedEvent);

    }

    @EventSourcingHandler
    public void on(PaymentProcessedEvent paymentProcessedEvent) {
        this.paymentId = paymentProcessedEvent.getPaymentId();
        this.orderId = paymentProcessedEvent.getOrderId();
    }

}
