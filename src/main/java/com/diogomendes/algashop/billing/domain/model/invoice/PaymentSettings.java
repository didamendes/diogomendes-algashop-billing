package com.diogomendes.algashop.billing.domain.model.invoice;

import com.diogomendes.algashop.billing.domain.model.DomainException;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.*;
import lombok.EqualsAndHashCode.Include;

import java.util.UUID;

import static com.diogomendes.algashop.billing.domain.model.IdGenerator.generateTimeBasedUUID;
import static com.diogomendes.algashop.billing.domain.model.invoice.PaymentMethod.CREDIT_CARD;
import static jakarta.persistence.EnumType.STRING;
import static java.util.Objects.requireNonNull;
import static lombok.AccessLevel.*;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Setter(PRIVATE)
@Getter
@NoArgsConstructor(access = PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor(access = PROTECTED)
@Entity
public class PaymentSettings {

    @Id
    @Include
    private UUID id;
    private UUID creditCardId;
    private String gatewayCode;

    @Enumerated(STRING)
    private PaymentMethod method;

    @Getter(PRIVATE)
    @Setter(PACKAGE)
    @OneToOne(mappedBy = "paymentSettings")
    private Invoice invoice;

    static PaymentSettings brandNew(PaymentMethod method, UUID creditCardId) {
        requireNonNull(method);

        if (method.equals(CREDIT_CARD)) {
            requireNonNull(creditCardId);
        }

        return new PaymentSettings(
                generateTimeBasedUUID(),
                creditCardId,
                null,
                method,
                null
        );
    }

    void assignGatewayCode(String gatewayCode) {
        if (isBlank(gatewayCode)) {
            throw new IllegalArgumentException();
        }

        if (this.getGatewayCode() != null) {
            throw new DomainException("Gateway code is already assigned");
        }

        setGatewayCode(gatewayCode);
    }
}
