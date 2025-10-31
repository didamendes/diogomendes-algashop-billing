package com.diogomendes.algashop.billing.domain.model;

import org.apache.commons.validator.routines.EmailValidator;

import static java.util.Objects.requireNonNull;

public class FieldValidations {
    private FieldValidations() {

    }

    public static void requiresNonBlank(String value) {
        requiresNonBlank(value, null);
    }

    public static void requiresNonBlank(String value, String errorMessage) {
        requireNonNull(value);
        if (value.isBlank()) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    public static void requiresValidEmail(String email) {
        requiresValidEmail(email, null);
    }

    public static void requiresValidEmail(String email, String errorMessage) {
        requireNonNull(email, errorMessage);
        if (email.isBlank()) {
            throw new IllegalArgumentException(errorMessage);
        }
        if (!EmailValidator.getInstance().isValid(email)) {
            throw new IllegalArgumentException(errorMessage);
        }
    }
}
