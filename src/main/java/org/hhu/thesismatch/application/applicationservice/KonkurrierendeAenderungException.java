package org.hhu.thesismatch.application.applicationservice;

/**
 * Wird geworfen, wenn ein Betreuer zwischenzeitlich von jemand anderem geändert wurde
 * (Optimistic Locking, verhindert Lost Updates).
 */
public class KonkurrierendeAenderungException extends RuntimeException {

    public KonkurrierendeAenderungException() {
        super("Der Datensatz wurde zwischenzeitlich geändert. Bitte neu laden.");
    }
}
