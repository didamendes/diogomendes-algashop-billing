package com.diogomendes.algashop.billing.domain.model.invoice;

import com.diogomendes.algashop.billing.domain.model.FieldValidations;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.math.BigDecimal;

import static java.math.BigDecimal.ZERO;
import static java.util.Objects.requireNonNull;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Setter(PRIVATE)
@EqualsAndHashCode
@NoArgsConstructor(access = PROTECTED)
@Embeddable
public class LineItem {
    private Integer number;
    private String name;
    private BigDecimal amount;

    @Builder
    public LineItem(Integer number, String name, BigDecimal amount) {
        FieldValidations.requiresNonBlank(name);
        requireNonNull(number);
        requireNonNull(amount);

        if (amount.compareTo(ZERO) <= 0) {
            throw new IllegalArgumentException();
        }

        if (number <= 0) {
            throw new IllegalArgumentException();
        }

        this.number = number;
        this.name = name;
        this.amount = amount;
    }
}
