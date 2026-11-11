package org.hhu.thesismatch.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain configure(HttpSecurity http,
                                         RoleBasedSuccessHandler successHandler,
                                         AppUserService appUserService) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/home", "/css/**", "/error").permitAll()
                        .anyRequest().authenticated())
                .oauth2Login(login -> login
                        .userInfoEndpoint(info -> info.userService(appUserService))
                        .successHandler(successHandler));

        return http.build();
    }
}
