package org.hhu.chatgpt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain configure(HttpSecurity chainBuilder,RoleBasedSuccessHandler successHandler) throws Exception{
         chainBuilder.authorizeHttpRequests( configurer -> configurer.requestMatchers("/","/home","/css/*").permitAll()
//                         .requestMatchers("/admin/**").hasRole("ADMIN")
//                         .requestMatchers("/betreuer/**").hasRole("BETREUER")  //Zugriffsrechte über Controller festlegen
                    .anyRequest().authenticated())
                 .oauth2Login(config ->
                         config.userInfoEndpoint(
                         info -> info.userService(new AppUserService()
                         )).successHandler(successHandler));
                 //.oauth2Login(oauth -> oauth.defaultSuccessUrl("/home",true)); // nach der Author. wirft in /home Seite

         return chainBuilder.build();
    }
}
