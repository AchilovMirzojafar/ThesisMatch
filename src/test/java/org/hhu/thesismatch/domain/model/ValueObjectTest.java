package org.hhu.thesismatch.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ValueObjectTest {

    @Test
    @DisplayName("GitHubId: zwei IDs mit gleichem Wert sind gleich")
    void gitHubId_equalByValue() {
        assertThat(new GitHubId(42)).isEqualTo(new GitHubId(42));
        assertThat(new GitHubId(42)).hasToString("42");
    }

    @Test
    @DisplayName("GitHubId: null, 0 und negative Werte werden abgelehnt")
    void gitHubId_rejectsInvalid() {
        assertThatThrownBy(() -> new GitHubId(0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new GitHubId(-1)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> GitHubId.of(null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Email: gültige Adresse wird getrimmt übernommen")
    void email_valid() {
        Email email = new Email("  max@example.com ");
        assertThat(email.value()).isEqualTo("max@example.com");
        assertThat(email).isEqualTo(new Email("max@example.com"));
    }

    @Test
    @DisplayName("Email: leere oder ungültige Adressen werden abgelehnt")
    void email_invalid() {
        assertThatThrownBy(() -> new Email(null)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Email(" ")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Email("kein-at-zeichen")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Email("a@b")).isInstanceOf(IllegalArgumentException.class);
    }
}
