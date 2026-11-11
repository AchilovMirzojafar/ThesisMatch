package org.hhu.thesismatch.domain.model;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Aggregate Root. Alle Änderungen an den Themen eines Betreuers laufen über diese Klasse,
 * damit die Invarianten des Aggregats an einer Stelle geprüft werden.
 */
public final class Betreuer {

    public static final int MAX_NAME_LENGTH = 50;
    public static final String PHONE_REGEX = "^[0-9+\\-\\s]{6,12}$";
    private static final Pattern PHONE_PATTERN = Pattern.compile(PHONE_REGEX);

    private final GitHubId fachId;
    private final Integer version;

    private String titel;
    private String firstName;
    private String lastName;
    private Email email;
    private String phone;
    private Set<String> tags;

    private final Map<UUID, Thema> themen = new LinkedHashMap<>();

    private Betreuer(GitHubId fachId, Integer version) {
        this.fachId = Objects.requireNonNull(fachId, "fachId");
        this.version = version;
    }

    /**
     * Legt einen neuen Betreuer ohne Telefonnummer, Tags und Themen an.
     */
    public static Betreuer neu(GitHubId fachId, String titel, String firstName, String lastName, Email email) {
        Betreuer betreuer = new Betreuer(fachId, null);
        betreuer.updateProfil(titel, firstName, lastName, email, null, Set.of());
        return betreuer;
    }

    /**
     * Stellt einen gespeicherten Betreuer wieder her. Nur für die Persistenzschicht gedacht.
     */
    public static Betreuer rekonstruiere(GitHubId fachId, Integer version, String titel, String firstName,
                                         String lastName, Email email, String phone, Set<String> tags,
                                         Collection<Thema> themen) {
        Betreuer betreuer = new Betreuer(fachId, version);
        betreuer.titel = titel;
        betreuer.firstName = firstName;
        betreuer.lastName = lastName;
        betreuer.email = email;
        betreuer.phone = phone;
        betreuer.tags = copy(tags);
        if (themen != null) {
            themen.forEach(t -> betreuer.themen.put(t.getId(), t));
        }
        return betreuer;
    }

    public void updateProfil(String titel, String firstName, String lastName, Email email,
                             String phone, Set<String> tags) {
        this.titel = requireName(titel, "Titel");
        this.firstName = requireName(firstName, "Vorname");
        this.lastName = requireName(lastName, "Nachname");
        this.email = Objects.requireNonNull(email, "email");
        this.phone = normalizePhone(phone);
        this.tags = copy(tags);
    }

    public void addThema(Thema thema) {
        Objects.requireNonNull(thema, "thema");
        if (themen.containsKey(thema.getId())) {
            throw new IllegalArgumentException("Thema existiert bereits: " + thema.getId());
        }
        themen.put(thema.getId(), thema);
    }

    /**
     * @return true, wenn das Thema zu diesem Betreuer gehörte und entfernt wurde
     */
    public boolean removeThema(UUID themaId) {
        return themen.remove(themaId) != null;
    }

    private static String requireName(String value, String feld) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(feld + " darf nicht leer sein");
        }
        String trimmed = value.trim();
        if (trimmed.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException(feld + " darf maximal " + MAX_NAME_LENGTH + " Zeichen haben");
        }
        return trimmed;
    }

    private static String normalizePhone(String phone) {
        if (phone == null || phone.isBlank()) {
            return null;
        }
        String trimmed = phone.trim();
        if (!PHONE_PATTERN.matcher(trimmed).matches()) {
            throw new IllegalArgumentException("Ungültige Telefonnummer: " + phone);
        }
        return trimmed;
    }

    private static Set<String> copy(Set<String> values) {
        return values == null ? Set.of() : Collections.unmodifiableSet(new LinkedHashSet<>(values));
    }

    public GitHubId getFachId() {
        return fachId;
    }

    /**
     * Version für Optimistic Locking. {@code null} bei einem noch nicht gespeicherten Betreuer.
     */
    public Integer getVersion() {
        return version;
    }

    public String getTitel() {
        return titel;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public Email getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public Set<String> getTags() {
        return tags;
    }

    public List<Thema> getThemen() {
        return List.copyOf(themen.values());
    }
}
