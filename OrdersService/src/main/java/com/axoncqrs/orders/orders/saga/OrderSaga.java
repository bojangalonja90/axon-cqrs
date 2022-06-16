package com.axoncqrs.orders.orders.saga;

import com.axoncqrs.corelib.commands.CancelProductReservationCommand;
import com.axoncqrs.corelib.commands.ProcessPaymentCommand;
import com.axoncqrs.corelib.commands.ReseveProductCommand;
import com.axoncqrs.corelib.events.PaymentProcessedEvent;
import com.axoncqrs.corelib.events.ProductReservationCancelledEvent;
import com.axoncqrs.corelib.events.ProductReservedEvent;
import com.axoncqrs.corelib.model.User;
import com.axoncqrs.corelib.query.FetchUserPaymentDetailsQuery;
import com.axoncqrs.orders.orders.command.ApproveOrderCommand;
import com.axoncqrs.orders.orders.command.RejectOrderCommand;
import com.axoncqrs.orders.orders.core.events.OrderApprovedEvent;
import com.axoncqrs.orders.orders.core.events.OrderCreatedEvent;
import com.axoncqrs.orders.orders.core.events.OrderRejectedEvent;
import org.axonframework.commandhandling.CommandCallback;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.CommandResultMessage;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.spring.stereotype.Saga;
import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Saga
public class OrderSaga {

    Logger LOG = LoggerFactory.getLogger(OrderSaga.class);

    @Autowired
    private transient CommandGateway commandGateway;

    @Autowired
    private transient QueryGateway queryGateway;

    @StartSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderCreatedEvent orderCreatedEvent) {
        ReseveProductCommand reseveProductCommand = ReseveProductCommand.builder()
                .orderId(orderCreatedEvent.getOrderId())
                .productId(orderCreatedEvent.getProductId())
                .quantity(orderCreatedEvent.getQuantity())
                .userId(orderCreatedEvent.getUserId())
                .build();

        LOG.info("OrderCreatedEvent for orderId: " + orderCreatedEvent.getOrderId() +
                " and productId: " + orderCreatedEvent.getProductId());

        commandGateway.send(reseveProductCommand, new CommandCallback<ReseveProductCommand, Object>() {
            @Override
            public void onResult(CommandMessage<? extends ReseveProductCommand> commandMessage, CommandResultMessage<?> commandResultMessage) {
                if (commandResultMessage.isExceptional()) {
                    LOG.info("Compensating");
                }
            }
        });
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(ProductReservedEvent productReservedEvent) {
        //Process user payment
        LOG.info("Processing user payment");
        FetchUserPaymentDetailsQuery fetchUserPaymentDetailsQuery =
                new FetchUserPaymentDetailsQuery(productReservedEvent.getUserId());

        User userPaymentDetails = null;
        try {
            userPaymentDetails = queryGateway.query(fetchUserPaymentDetailsQuery, ResponseTypes.instanceOf(User.class)).join();
        } catch (Exception ex) {
            LOG.error(ex.getMessage());
            //start compensating transaction
            cancelProductReservation(productReservedEvent, ex.getMessage());
            return;
        }

        if (userPaymentDetails == null) {
            //start compensating transacion
            cancelProductReservation(productReservedEvent, "Could not fetch user payment details");
            return;
        }

        LOG.info("Successfully fetched user payment details: ", userPaymentDetails.getFirstName());

        ProcessPaymentCommand processPaymentCommand = ProcessPaymentCommand.builder()
                .orderId(productReservedEvent.getOrderId())
                .paymentDetails(userPaymentDetails.getPaymentDetails())
                .paymentId(UUID.randomUUID().toString())
                .build();

        String result = null;
        try {
            result = commandGateway.sendAndWait(processPaymentCommand, 10, TimeUnit.SECONDS);
        } catch (Exception ex) {
            LOG.error("Error processing payment : " + ex.getMessage());
            //Start compensating transaction
            cancelProductReservation(productReservedEvent, ex.getMessage());
        }

        if (result == null) {
            LOG.info("The Process payment result is NULL. Inititalizing compensating transaction");
            cancelProductReservation(productReservedEvent,
                    "Could not process user payment for provided payment details");
        }
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(PaymentProcessedEvent paymentProcessedEvent) {
        //Approve order
        ApproveOrderCommand approveOrderCommand = new ApproveOrderCommand(paymentProcessedEvent.getOrderId());
        commandGateway.send(approveOrderCommand);
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderApprovedEvent orderApprovedEvent) {
        LOG.info("Order is approved, and saga is complete for order Id: " + orderApprovedEvent.getOrderId());
    }

    private void cancelProductReservation(ProductReservedEvent productReservedEvent, String reason) {

        CancelProductReservationCommand cancelProductReservationCommand = CancelProductReservationCommand.builder()
                .orderId(productReservedEvent.getOrderId())
                .productId(productReservedEvent.getProductId())
                .quantity(productReservedEvent.getQuantity())
                .userId(productReservedEvent.getUserId())
                .reason(reason)
                .build();

        commandGateway.send(cancelProductReservationCommand);

    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(ProductReservationCancelledEvent productReservationCancelledEvent) {
        //send RejectOrder command
        RejectOrderCommand rejectOrderCommand = new RejectOrderCommand(productReservationCancelledEvent.getOrderId(),
                productReservationCancelledEvent.getReason());

        commandGateway.send(rejectOrderCommand);
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderRejectedEvent orderRejectedEvent) {
        LOG.info("Order " + orderRejectedEvent.getOrderId() + " has been rejected: " + orderRejectedEvent.getReason());
    }
}
