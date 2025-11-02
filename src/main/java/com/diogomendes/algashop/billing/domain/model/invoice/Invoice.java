package com.diogomendes.algashop.billing.domain.model.invoice;

import com.diogomendes.algashop.billing.domain.model.AbstractAuditableEntityAggregateRoot;
import com.diogomendes.algashop.billing.domain.model.DomainException;
import jakarta.persistence.*;
import lombok.*;
import lombok.EqualsAndHashCode.Include;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.*;

import static com.diogomendes.algashop.billing.domain.model.IdGenerator.generateTimeBasedUUID;
import static com.diogomendes.algashop.billing.domain.model.invoice.InvoiceStatus.*;
import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.EAGER;
import static java.math.BigDecimal.ZERO;
import static java.time.OffsetDateTime.now;
import static java.util.Objects.requireNonNull;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Setter(PRIVATE)
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PROTECTED)
@Entity
public class Invoice extends AbstractAuditableEntityAggregateRoot<Invoice> {

    @Include
    @Id
    private UUID id;
    private String orderId;
    private UUID customerId;

    private OffsetDateTime issuedAt;
    private OffsetDateTime paidAt;
    private OffsetDateTime canceledAt;
    private OffsetDateTime expiresAt;

    private BigDecimal totalAmount;

    @Enumerated(STRING)
    private InvoiceStatus status;

    @OneToOne(cascade = ALL, fetch = EAGER, orphanRemoval = true)
    private PaymentSettings paymentSettings;

    @ElementCollection
    @CollectionTable(name = "invoice_line_items", joinColumns = @JoinColumn(name = "invoice_id"))
    private Set<LineItem> items = new HashSet<>();

    @Embedded
    private Payer payer;
    private String cancelReason;

    public static Invoice issue(String orderId, UUID customerId, Payer payer, Set<LineItem> items) {

        requireNonNull(customerId);
        requireNonNull(payer);
        requireNonNull(items);

        if (isBlank(orderId)) {
            throw new IllegalArgumentException();
        }

        if (items.isEmpty()) {
            throw new IllegalArgumentException();
        }

        BigDecimal totalAmount = items.stream().map(LineItem::getAmount).reduce(ZERO, BigDecimal::add);

        Invoice invoice = new Invoice(
                generateTimeBasedUUID(),
                orderId,
                customerId,
                now(),
                null,
                null,
                now().plusDays(3),
                totalAmount,
                UNPAID,
                null,
                items,
                payer,
                null
        );

        invoice.registerEvent(new InvoiceIssuedEvent(invoice.getId(), invoice.getCustomerId(),
                invoice.getOrderId(), invoice.getIssuedAt()));

        return invoice;
    }

    public Set<LineItem> getItems() {
        return Collections.unmodifiableSet(this.items);
    }

    public boolean isCanceled() {
        return CANCELED.equals(this.getStatus());
    }

    public boolean isUnpaid() {
        return UNPAID.equals(this.getStatus());
    }

    public boolean isPaid() {
        return PAID.equals(this.getStatus());
    }

    public void markAsPaid() {
        if (!isUnpaid()) {
            throw new DomainException(String.format("Invoice %s with status %s cannot be marked as paid",
                    this.getId(), this.getStatus().toString().toLowerCase()));
        }
        setPaidAt(now());
        setStatus(PAID);
        registerEvent(new InvoicePaidEvent(this.getId(), this.getCustomerId(), this.getOrderId(),
                this.getPaidAt()));
    }

    public void cancel(String cancelReason) {
        if (isCanceled()) {
            throw new DomainException(String.format("Invoice %s is already canceled", this.getId()));
        }

        setCancelReason(cancelReason);
        setCanceledAt(now());
        setStatus(CANCELED);
        registerEvent(new InvoiceCanceledEvent(this.getId(), this.getCustomerId(), this.getOrderId(),
                this.getCanceledAt()));
    }

    public void assignPaymentGatewayCode(String code) {
        if (!isUnpaid()) {
            throw new DomainException(String.format("Invoice %s with status %s cannot be edited",
                    this.getId(), this.getStatus().toString().toLowerCase()));
        }

        if (this.getPaymentSettings() == null) { //Nova validação
            throw new DomainException("Invoice has no payment settings");
        }

        this.getPaymentSettings().assignGatewayCode(code);
    }

    public void changePaymentSettings(PaymentMethod method, UUID creditCardId) {
        if (!isUnpaid()) {
            throw new DomainException(String.format("Invoice %s with status %s cannot be edited",
                    this.getId(), this.getStatus().toString().toLowerCase()));
        }

        PaymentSettings paymentSettings = PaymentSettings.brandNew(method, creditCardId);
        paymentSettings.setInvoice(this);
        this.setPaymentSettings(paymentSettings);
    }

}
