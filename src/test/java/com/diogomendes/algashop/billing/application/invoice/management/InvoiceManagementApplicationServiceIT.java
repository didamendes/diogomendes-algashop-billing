package com.diogomendes.algashop.billing.application.invoice.management;

import com.diogomendes.algashop.billing.domain.model.creditcard.CreditCard;
import com.diogomendes.algashop.billing.domain.model.creditcard.CreditCardCreditCardRepository;
import com.diogomendes.algashop.billing.domain.model.invoice.*;
import com.diogomendes.algashop.billing.domain.model.invoice.payment.Payment;
import com.diogomendes.algashop.billing.domain.model.invoice.payment.PaymentGatewayService;
import com.diogomendes.algashop.billing.domain.model.invoice.payment.PaymentRequest;
import com.diogomendes.algashop.billing.domain.model.invoice.payment.PaymentStatus;
import com.diogomendes.algashop.billing.infrastructure.listener.InvoiceEventListener;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.diogomendes.algashop.billing.application.invoice.management.GenerateInvoiceInputTestDataBuilder.anInput;
import static com.diogomendes.algashop.billing.domain.model.creditcard.CreditCardTestDataBuilder.aCreditCard;
import static com.diogomendes.algashop.billing.domain.model.invoice.InvoiceStatus.UNPAID;
import static com.diogomendes.algashop.billing.domain.model.invoice.InvoiceTestDataBuilder.anInvoice;
import static com.diogomendes.algashop.billing.domain.model.invoice.PaymentMethod.CREDIT_CARD;
import static com.diogomendes.algashop.billing.domain.model.invoice.PaymentMethod.GATEWAY_BALANCE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
class InvoiceManagementApplicationServiceIT {

    @Autowired
    private InvoiceManagementApplicationService applicationService;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private CreditCardCreditCardRepository creditCardRepository;

    @MockitoSpyBean
    private InvoicingService invoicingService;

    @MockitoBean
    private PaymentGatewayService paymentGatewayService;

    @MockitoSpyBean
    private InvoiceEventListener invoiceEventListener;

    @Test
    public void shouldGenerateInvoiceWithCreditCardAsPayment() {
        UUID customerId = UUID.randomUUID();
        CreditCard creditCard = aCreditCard().build();
        creditCardRepository.saveAndFlush(creditCard);

        GenerateInvoiceInput input = anInput().build();

        input.setPaymentSettings(
                PaymentSettingsInput.builder()
                        .creditCardId(creditCard.getId())
                        .method(CREDIT_CARD)
                        .build()
        );

        UUID invoiceId = applicationService.generate(input);

        Invoice invoice = invoiceRepository.findById(invoiceId).orElseThrow();

        assertThat(invoice.getStatus()).isEqualTo(UNPAID);
        assertThat(invoice.getOrderId()).isEqualTo(input.getOrderId());

        assertThat(invoice.getVersion()).isEqualTo(0L);
        assertThat(invoice.getCreatedAt()).isNotNull();
        assertThat(invoice.getCreatedByUserId()).isNotNull();

        verify(invoicingService).issue(any(), any(), any(), any());

        verify(invoiceEventListener).listen(any(InvoiceIssuedEvent.class));
    }

    @Test
    public void shouldGenerateInvoiceWithGatewayBalanceAsPayment() {
        UUID customerId = UUID.randomUUID();
        CreditCard creditCard = aCreditCard().build();
        creditCardRepository.saveAndFlush(creditCard);

        GenerateInvoiceInput input = anInput().build();

        input.setPaymentSettings(
                PaymentSettingsInput.builder()
                        .creditCardId(creditCard.getId())
                        .method(GATEWAY_BALANCE)
                        .build()
        );

        UUID invoiceId = applicationService.generate(input);

        Invoice invoice = invoiceRepository.findById(invoiceId).orElseThrow();

        assertThat(invoice.getStatus()).isEqualTo(UNPAID);
        assertThat(invoice.getOrderId()).isEqualTo(input.getOrderId());

        verify(invoicingService).issue(any(), any(), any(), any());
    }

    @Test
    public void shouldProcessInvoicePayment() {
        Invoice invoice = anInvoice().build();
        invoice.changePaymentSettings(GATEWAY_BALANCE, null);
        invoiceRepository.saveAndFlush(invoice);

        Payment payment = Payment.builder()
                .gatewayCode("12345")
                .invoiceId(invoice.getId())
                .method(invoice.getPaymentSettings().getMethod())
                .status(PaymentStatus.PAID)
                .build();

        when(paymentGatewayService.capture(any(PaymentRequest.class))).thenReturn(payment);

        applicationService.processPayment(invoice.getId());

        Invoice paidInvoice = invoiceRepository.findById(invoice.getId()).orElseThrow();

        assertThat(paidInvoice.isPaid()).isTrue();

        verify(paymentGatewayService).capture(any(PaymentRequest.class));
        verify(invoicingService).assignPayment(any(Invoice.class), any(Payment.class));

        verify(invoiceEventListener).listen(any(InvoicePaidEvent.class));
    }

    @Test
    public void shouldProcessInvoicePaymentAndCancelInvoice() {
        Invoice invoice = anInvoice().build();
        invoice.changePaymentSettings(GATEWAY_BALANCE, null);
        invoiceRepository.saveAndFlush(invoice);

        Payment payment = Payment.builder()
                .gatewayCode("12345")
                .invoiceId(invoice.getId())
                .method(invoice.getPaymentSettings().getMethod())
                .status(PaymentStatus.FAILED)
                .build();

        when(paymentGatewayService.capture(any(PaymentRequest.class))).thenReturn(payment);

        applicationService.processPayment(invoice.getId());

        Invoice paidInvoice = invoiceRepository.findById(invoice.getId()).orElseThrow();

        assertThat(paidInvoice.isCanceled()).isTrue();

        verify(paymentGatewayService).capture(any(PaymentRequest.class));
        verify(invoicingService).assignPayment(any(Invoice.class), any(Payment.class));

        verify(invoiceEventListener).listen(any(InvoiceCanceledEvent.class));
    }

}