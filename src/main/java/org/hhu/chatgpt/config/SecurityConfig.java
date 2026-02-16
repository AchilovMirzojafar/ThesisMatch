package org.hhu.chatgpt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain configure(HttpSecurity chainBuilder,RoleBasedSuccessHandler successHandler, AppUserService appUserService) throws Exception{
         chainBuilder.authorizeHttpRequests( configurer -> configurer.requestMatchers("/","/home","/css/*").permitAll()
                    .anyRequest().authenticated())
                 .oauth2Login(config ->
                         config.userInfoEndpoint(
                         info -> info.userService(appUserService
                         )).successHandler(successHandler));

         return chainBuilder.build();
    }
}
