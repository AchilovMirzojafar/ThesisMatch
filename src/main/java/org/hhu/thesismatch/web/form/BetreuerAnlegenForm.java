package org.hhu.thesismatch.web.form;

import org.hhu.thesismatch.domain.model.Betreuer;
import org.hhu.thesismatch.domain.model.Email;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * Formular des Admins zum Anlegen eines Betreuers.
 */
public class BetreuerAnlegenForm {

    @NotNull(message = "Darf nicht leer sein!")
    @Positive(message = "GitHub-ID muss positiv sein")
    private Integer fachId;

    @NotBlank(message = "Titel muss ausgewählt werden!")
    private String titel;

    @NotBlank(message = "Darf nicht leer sein!")
    @Size(max = Betreuer.MAX_NAME_LENGTH, message = "Maximal 50 Zeichen")
    private String firstName;

    @NotBlank(message = "Darf nicht leer sein!")
    @Size(max = Betreuer.MAX_NAME_LENGTH, message = "Maximal 50 Zeichen")
    private String lastName;

    @NotBlank(message = "E-Mail darf nicht leer sein")
    @Size(max = Email.MAX_LENGTH, message = "E-Mail ist zu lang")
    @jakarta.validation.constraints.Email(regexp = Email.REGEX,
            message = "Bitte eine gültige E-Mail-Adresse eingeben")
    private String email;

    public Integer getFachId() {
        return fachId;
    }

    public void setFachId(Integer fachId) {
        this.fachId = fachId;
    }

    public String getTitel() {
        return titel;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
