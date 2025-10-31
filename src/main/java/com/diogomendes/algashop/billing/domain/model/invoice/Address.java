package com.diogomendes.algashop.billing.domain.model.invoice;

import jakarta.persistence.Embeddable;
import lombok.*;

import static com.diogomendes.algashop.billing.domain.model.FieldValidations.requiresNonBlank;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Setter(PRIVATE)
@EqualsAndHashCode
@NoArgsConstructor(access = PROTECTED)
@Embeddable
public class Address {
    private String street;
    private String number;
    private String complement;
    private String neighborhood;
    private String city;
    private String state;
    private String zipCode;

    @Builder
    public Address(String street, String number, String complement, String neighborhood,
                   String city, String state, String zipCode) {
        requiresNonBlank(street);
        requiresNonBlank(neighborhood);
        requiresNonBlank(city);
        requiresNonBlank(number);
        requiresNonBlank(state);
        requiresNonBlank(zipCode);

        this.street = street;
        this.number = number;
        this.complement = complement;
        this.neighborhood = neighborhood;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
    }
}
