package com.diogomendes.algashop.billing.domain.model.creditcard;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CreditCardCreditCardRepository extends JpaRepository<CreditCard, UUID> {
}
