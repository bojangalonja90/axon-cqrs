package com.axoncqrs.payments.core;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "payments")
@Data
public class PaymentEntity {

    @Id
    private String paymentId;
    @Column
    public String orderId;

}
