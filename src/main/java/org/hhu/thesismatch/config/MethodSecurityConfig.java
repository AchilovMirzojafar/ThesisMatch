package org.hhu.thesismatch.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

/**
 * Aktiviert {@code @Secured} (für die Annotationen in config.annotations) und {@code @PreAuthorize}.
 */
@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class MethodSecurityConfig {
}
