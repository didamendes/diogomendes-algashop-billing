package com.diogomendes.algashop.billing.domain.model.creditcard;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;
import lombok.EqualsAndHashCode.Include;
import org.apache.commons.lang3.StringUtils;

import java.time.OffsetDateTime;
import java.util.UUID;

import static com.diogomendes.algashop.billing.domain.model.IdGenerator.generateTimeBasedUUID;
import static java.time.OffsetDateTime.now;
import static java.util.Objects.requireNonNull;
import static lombok.AccessLevel.*;
import static org.apache.commons.lang3.StringUtils.isAnyBlank;

@Setter(PRIVATE)
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PROTECTED)
@Entity
public class CreditCard {
    @Include
    @Id
    private UUID id;
    private OffsetDateTime createdAt;
    private UUID customerId;

    private String lastNumbers;
    private String brand;
    private Integer expMonth;
    private Integer expYear;

    private String gatewayCode;

    public static CreditCard brandNew(UUID customerId, String lastNumbers, String brand,
                                      Integer expMonth, Integer expYear, String gatewayCreditCardCode) {

        requireNonNull(customerId);
        requireNonNull(expMonth);
        requireNonNull(expYear);

        if (isAnyBlank(lastNumbers, brand, gatewayCreditCardCode)) {
            throw new IllegalArgumentException();
        }

        return new CreditCard(
                generateTimeBasedUUID(),
                now(),
                customerId,
                lastNumbers,
                brand,
                expMonth,
                expYear,
                gatewayCreditCardCode
        );
    }

    public void setGatewayCode(String gatewayCode) {
        if (StringUtils.isBlank(gatewayCode)) {
            throw new IllegalArgumentException();
        }
        this.gatewayCode = gatewayCode;
    }

}
