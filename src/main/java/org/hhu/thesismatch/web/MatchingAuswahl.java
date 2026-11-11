package org.hhu.thesismatch.web;

import java.util.List;

/**
 * View-Modell für die Matching-Seite: alle auswählbaren Kurse und Interessen.
 */
public record MatchingAuswahl(List<String> kurse, List<String> interessen) {
}
