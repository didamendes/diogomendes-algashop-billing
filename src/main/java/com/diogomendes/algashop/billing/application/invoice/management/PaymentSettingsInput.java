package com.diogomendes.algashop.billing.application.invoice.management;

import com.diogomendes.algashop.billing.domain.model.invoice.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentSettingsInput {
    private PaymentMethod method;
    private UUID creditCardId;
}
