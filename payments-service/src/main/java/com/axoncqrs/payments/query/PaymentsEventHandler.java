package com.axoncqrs.payments.query;

import com.axoncqrs.corelib.events.PaymentProcessedEvent;
import com.axoncqrs.payments.aggregate.PaymentAggregate;
import com.axoncqrs.payments.core.PaymentEntity;
import com.axoncqrs.payments.core.PaymentRepository;
import org.axonframework.eventhandling.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PaymentsEventHandler {

    Logger LOG = LoggerFactory.getLogger(PaymentsEventHandler.class);

    @Autowired
    private final PaymentRepository paymentRepository;

    public PaymentsEventHandler(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @EventHandler
    public void handle(PaymentProcessedEvent paymentProcessedEvent) {

        LOG.info("Persisting payment info: " + paymentProcessedEvent.getPaymentId());

        PaymentEntity paymentEntity = new PaymentEntity();
        paymentEntity.setPaymentId(paymentProcessedEvent.getPaymentId());
        paymentEntity.setOrderId(paymentEntity.getOrderId());

        paymentRepository.save(paymentEntity);

    }

}
