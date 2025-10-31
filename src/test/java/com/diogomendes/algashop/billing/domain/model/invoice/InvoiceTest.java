package com.diogomendes.algashop.billing.domain.model.invoice;

import com.diogomendes.algashop.billing.domain.model.DomainException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.diogomendes.algashop.billing.domain.model.invoice.InvoiceStatus.*;
import static com.diogomendes.algashop.billing.domain.model.invoice.InvoiceTestDataBuilder.*;
import static com.diogomendes.algashop.billing.domain.model.invoice.PaymentMethod.CREDIT_CARD;
import static com.diogomendes.algashop.billing.domain.model.invoice.PaymentMethod.GATEWAY_BALANCE;
import static java.math.BigDecimal.ZERO;
import static org.assertj.core.api.Assertions.*;

class InvoiceTest {

    @Test
    public void shouldMarkInvoiceAsPaidWhenUnpaid() {
        Invoice invoice = anInvoice().build();
        invoice.changePaymentSettings(GATEWAY_BALANCE, null);

        invoice.markAsPaid();

        assertWith(invoice,
                i -> assertThat(i.isPaid()).isTrue(),
                i -> assertThat(i.getPaidAt()).isNotNull()
                );
    }

    @Test
    public void shouldCancelInvoiceWithReason() {
        Invoice invoice = anInvoice().build();
        String cancelReason = "Customer requested cancellation";

        invoice.cancel(cancelReason);

        assertWith(invoice,
                i -> assertThat(i.isCanceled()).isTrue(),
                i -> assertThat(i.getCanceledAt()).isNotNull(),
                i -> assertThat(i.getCancelReason()).isEqualTo(cancelReason)
        );
    }

    @Test
    public void shouldIssueInvoiceCorrectly() {
        String orderId = "123";
        UUID customerId = UUID.randomUUID();
        Payer payer = aPayer();
        Set<LineItem> items = new HashSet<>();
        items.add(aLineItem());
        items.add(aLineItemAlt());

        Invoice invoice = Invoice.issue(orderId, customerId, payer, items);

        BigDecimal expectedTotalAmount = invoice.getItems()
                .stream()
                .map(LineItem::getAmount)
                .reduce(ZERO, BigDecimal::add);

        assertWith(invoice,
                i -> assertThat(i.getId()).isNotNull(),
                i -> assertThat(i.getTotalAmount()).isEqualTo(expectedTotalAmount),
                i -> assertThat(i.getStatus()).isEqualTo(UNPAID)
                );
    }

    @Test
    public void shouldThrowExceptionWhenIssuingInvoiceWithEmptyItems() {
        Set<LineItem> emptyItems = new HashSet<>();
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> Invoice.issue("12226N08",
                        UUID.randomUUID(),
                        aPayer(),
                        emptyItems));
    }

    @Test
    public void shouldChangePaymentSettingsWhenUnpaid() {
        Invoice invoice = anInvoice().build();
        UUID creditCardId = UUID.randomUUID();
        invoice.changePaymentSettings(CREDIT_CARD, creditCardId);

        assertWith(invoice,
                i -> assertThat(i.getPaymentSettings()).isNotNull(),
                i -> assertThat(i.getPaymentSettings().getMethod()).isEqualTo(CREDIT_CARD),
                i -> assertThat(i.getPaymentSettings().getCreditCardId()).isEqualTo(creditCardId));
    }

    @Test
    public void shouldThrowExceptionWhenChangingPaymentSettingsToPaidInvoice() {
        Invoice invoice = anInvoice().status(PAID).build();
        assertThatExceptionOfType(DomainException.class)
                .isThrownBy(() -> invoice.changePaymentSettings(CREDIT_CARD, UUID.randomUUID()));
    }

    @Test
    public void shouldThrowExceptionWhenMarkingCanceledInvoiceAsPaid() {
        Invoice invoice = anInvoice().status(CANCELED).build();

        assertThatExceptionOfType(DomainException.class)
                .isThrownBy(invoice::markAsPaid);
    }

    @Test
    public void shouldThrowExceptionWhenCancelingAlreadyCanceledInvoice() {
        Invoice invoice = anInvoice().status(CANCELED).build();

        assertThatExceptionOfType(DomainException.class)
                .isThrownBy(() -> invoice.cancel("Another reason"));
    }

    @Test
    public void shouldAssignPaymentGatewayCodeWhenUnpaid() {
        Invoice invoice = anInvoice().paymentSettings(CREDIT_CARD, UUID.randomUUID()).build();
        String gatewayCode = "code-from-gateway";

        invoice.assignPaymentGatewayCode(gatewayCode);

        assertThat(invoice.getPaymentSettings().getGatewayCode()).isEqualTo(gatewayCode);
    }

    @Test
    public void shouldThrowExceptionWhenAssigningGatewayCodeToPaidInvoice() {
        Invoice invoice = anInvoice().status(PAID).build();
        assertThatExceptionOfType(DomainException.class)
                .isThrownBy(() -> invoice.assignPaymentGatewayCode("some-code"));
    }

    @Test
    public void shouldThrowExceptionWhenTryingToModifyItemsSet() {
        Invoice invoice = anInvoice().build();
        Set<LineItem> items = invoice.getItems();

        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(items::clear);
    }

}