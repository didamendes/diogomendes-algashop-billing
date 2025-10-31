package com.diogomendes.algashop.billing.domain.model.invoice;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.*;

import static com.diogomendes.algashop.billing.domain.model.FieldValidations.requiresNonBlank;
import static com.diogomendes.algashop.billing.domain.model.FieldValidations.requiresValidEmail;
import static java.util.Objects.requireNonNull;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Setter(PRIVATE)
@EqualsAndHashCode
@NoArgsConstructor(access = PROTECTED)
@Embeddable
public class Payer {
    private String fullName;
    private String document;
    private String phone;
    private String email;

    @Embedded
    private Address address;

    @Builder
    public Payer(String fullName, String document, String phone, String email, Address address) {
        requiresNonBlank(fullName);
        requiresNonBlank(document);
        requiresNonBlank(phone);
        requiresValidEmail(email);
        requireNonNull(address);

        this.fullName = fullName;
        this.document = document;
        this.phone = phone;
        this.email = email;
        this.address = address;
    }
}
