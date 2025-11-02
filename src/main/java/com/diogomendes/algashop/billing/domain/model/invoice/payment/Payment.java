package com.diogomendes.algashop.billing.domain.model.invoice.payment;

import com.diogomendes.algashop.billing.domain.model.invoice.PaymentMethod;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

import static com.diogomendes.algashop.billing.domain.model.FieldValidations.requiresNonBlank;
import static java.util.Objects.requireNonNull;

@Getter
@Builder
@EqualsAndHashCode
public class Payment {
    private String gatewayCode;
    private UUID invoiceId;
    private PaymentMethod method;
    private PaymentStatus status;

    public Payment(String gatewayCode, UUID invoiceId,
                   PaymentMethod method, PaymentStatus status) {
        requiresNonBlank(gatewayCode);
        requireNonNull(invoiceId);
        requireNonNull(method);
        requireNonNull(status);

        this.gatewayCode = gatewayCode;
        this.invoiceId = invoiceId;
        this.method = method;
        this.status = status;
    }
}
