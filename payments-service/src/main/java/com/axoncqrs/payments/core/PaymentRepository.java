package com.axoncqrs.payments.core;

import org.springframework.data.repository.CrudRepository;

public interface PaymentRepository extends CrudRepository<PaymentEntity, String> {
}
