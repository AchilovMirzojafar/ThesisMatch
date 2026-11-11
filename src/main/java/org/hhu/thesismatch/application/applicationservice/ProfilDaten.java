package org.hhu.thesismatch.application.applicationservice;

import java.util.Set;

import org.hhu.thesismatch.domain.model.Email;

/**
 * Eingabedaten für die Änderung eines Betreuerprofils.
 */
public record ProfilDaten(
        String titel,
        String firstName,
        String lastName,
        Email email,
        String phone,
        Set<String> tags) {
}
