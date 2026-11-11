package org.hhu.thesismatch.domain.model;

import java.util.regex.Pattern;

/**
 * Value Object: E-Mail-Adresse. Ein Objekt dieser Klasse ist immer gültig.
 */
public record Email(String value) {

    public static final int MAX_LENGTH = 254;
    public static final String REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final Pattern PATTERN = Pattern.compile(REGEX);

    public Email {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("E-Mail darf nicht leer sein");
        }
        value = value.trim();
        if (value.length() > MAX_LENGTH || !PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("Ungültige E-Mail-Adresse: " + value);
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
