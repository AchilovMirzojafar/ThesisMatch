package org.hhu.chatgpt.config.annotations;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.TYPE})
@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_BETREUER')") //Zugriff, wenn eine der beiden Rollen vorhanden ist
public @interface AdminOrBetreuerOnly {
}
