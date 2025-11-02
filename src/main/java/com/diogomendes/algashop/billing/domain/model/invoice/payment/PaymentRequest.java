package com.diogomendes.algashop.billing.domain.model.invoice.payment;

import com.diogomendes.algashop.billing.domain.model.invoice.Payer;
import com.diogomendes.algashop.billing.domain.model.invoice.PaymentMethod;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

import static com.diogomendes.algashop.billing.domain.model.invoice.PaymentMethod.CREDIT_CARD;
import static java.util.Objects.requireNonNull;

@Getter
@EqualsAndHashCode
@Builder
public class PaymentRequest {
    private PaymentMethod method;
    private BigDecimal amount;
    private UUID invoiceId;
    private UUID creditCardId;
    private Payer payer;

    public PaymentRequest(PaymentMethod method, BigDecimal amount,
                          UUID invoiceId, UUID creditCardId, Payer payer) {
        requireNonNull(method);
        requireNonNull(amount);
        requireNonNull(invoiceId);
        requireNonNull(payer);

        if (method.equals(CREDIT_CARD)) {
            requireNonNull(creditCardId);
        }

        this.method = method;
        this.amount = amount;
        this.invoiceId = invoiceId;
        this.creditCardId = creditCardId;
        this.payer = payer;
    }
}
