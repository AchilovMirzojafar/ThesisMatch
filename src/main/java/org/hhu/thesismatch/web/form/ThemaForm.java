package org.hhu.thesismatch.web.form;

import java.util.LinkedHashSet;
import java.util.Set;

import org.hhu.thesismatch.domain.model.Thema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

/**
 * Formular zum Anlegen eines neuen Themas.
 */
public class ThemaForm {

    @NotBlank(message = "Titel darf nicht leer sein.")
    @Size(max = Thema.MAX_TITLE_LENGTH, message = "Titel darf maximal 50 Zeichen haben.")
    private String title;

    @Size(max = Thema.MAX_DESCRIPTION_LENGTH, message = "Beschreibung darf maximal 300 Zeichen haben.")
    private String description;

    @NotEmpty(message = "Bitte mindestens einen Tag auswählen.")
    private Set<String> tags = new LinkedHashSet<>();

    @NotEmpty(message = "Bitte mindestens eine Veranstaltung auswählen.")
    private Set<String> requirements = new LinkedHashSet<>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags == null ? new LinkedHashSet<>() : new LinkedHashSet<>(tags);
    }

    public Set<String> getRequirements() {
        return requirements;
    }

    public void setRequirements(Set<String> requirements) {
        this.requirements = requirements == null ? new LinkedHashSet<>() : new LinkedHashSet<>(requirements);
    }
}
