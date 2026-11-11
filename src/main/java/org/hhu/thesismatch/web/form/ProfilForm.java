package org.hhu.thesismatch.web.form;

import java.util.LinkedHashSet;
import java.util.Set;

import org.hhu.thesismatch.application.applicationservice.ProfilDaten;
import org.hhu.thesismatch.domain.model.Betreuer;
import org.hhu.thesismatch.domain.model.Email;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Formular, mit dem ein Betreuer sein eigenes Profil bearbeitet.
 * Die GitHub-ID wird nur angezeigt und nie aus dem Request übernommen.
 */
public class ProfilForm {

    private Integer fachId;

    private Integer version;

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

    @Pattern(regexp = "^$|" + Betreuer.PHONE_REGEX, message = "Ungültiges Telefonnummernformat")
    private String phone;

    private Set<String> tags = new LinkedHashSet<>();

    public static ProfilForm from(Betreuer betreuer) {
        ProfilForm form = new ProfilForm();
        form.fachId = betreuer.getFachId().value();
        form.version = betreuer.getVersion();
        form.titel = betreuer.getTitel();
        form.firstName = betreuer.getFirstName();
        form.lastName = betreuer.getLastName();
        form.email = betreuer.getEmail().value();
        form.phone = betreuer.getPhone();
        form.tags = new LinkedHashSet<>(betreuer.getTags());
        return form;
    }

    public ProfilDaten toProfilDaten() {
        return new ProfilDaten(titel, firstName, lastName, new Email(email), phone, tags);
    }

    public Integer getFachId() {
        return fachId;
    }

    public void setFachId(Integer fachId) {
        this.fachId = fachId;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags == null ? new LinkedHashSet<>() : new LinkedHashSet<>(tags);
    }
}
