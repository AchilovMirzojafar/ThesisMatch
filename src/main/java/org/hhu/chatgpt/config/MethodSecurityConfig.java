package org.hhu.chatgpt.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true) // aktiviert @Secured und @PreAuthorize
public class MethodSecurityConfig {
}
