package com.diogomendes.algashop.billing.application.invoice.query;

import com.diogomendes.algashop.billing.domain.model.invoice.Invoice;
import com.diogomendes.algashop.billing.domain.model.invoice.InvoiceRepository;
import com.diogomendes.algashop.billing.domain.model.invoice.InvoiceTestDataBuilder;
import com.diogomendes.algashop.billing.domain.model.invoice.PaymentMethod;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static com.diogomendes.algashop.billing.domain.model.invoice.PaymentMethod.GATEWAY_BALANCE;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class InvoiceQueryServiceIT {

    @Autowired
    private InvoiceQueryService service;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Test
    public void shouldFindByOrderId() {
        Invoice invoice = InvoiceTestDataBuilder.anInvoice().build();
        invoice.changePaymentSettings(GATEWAY_BALANCE, null);
        invoiceRepository.saveAndFlush(invoice);

        InvoiceOutput invoiceOutput = service.findByOrderId(invoice.getOrderId());

        assertThat(invoiceOutput.getId()).isEqualTo(invoice.getId());
    }

}