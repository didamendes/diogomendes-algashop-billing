package com.diogomendes.algashop.billing.infrastructure.payment;

import com.diogomendes.algashop.billing.domain.model.invoice.payment.Payment;
import com.diogomendes.algashop.billing.domain.model.invoice.payment.PaymentGatewayService;
import com.diogomendes.algashop.billing.domain.model.invoice.payment.PaymentRequest;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.diogomendes.algashop.billing.domain.model.invoice.PaymentMethod.GATEWAY_BALANCE;
import static com.diogomendes.algashop.billing.domain.model.invoice.payment.PaymentStatus.PAID;

@Service
public class PaymentGatewayServiceFakeImpl implements PaymentGatewayService {
    @Override
    public Payment capture(PaymentRequest request) {
        return Payment.builder()
                .invoiceId(request.getInvoiceId())
                .status(PAID)
                .method(request.getMethod())
                .gatewayCode(UUID.randomUUID().toString())
                .build();
    }

    @Override
    public Payment findByCode(String gatewayCode) {
        return Payment.builder()
                .invoiceId(UUID.randomUUID())
                .status(PAID)
                .method(GATEWAY_BALANCE)
                .gatewayCode(UUID.randomUUID().toString())
                .build();
    }
}
