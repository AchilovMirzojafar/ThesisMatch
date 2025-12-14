package org.hhu.chatgpt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain configure(HttpSecurity chainBuilder) throws Exception{
         chainBuilder.authorizeHttpRequests(
                configurer -> configurer.requestMatchers("/css/*").permitAll()
                .anyRequest().authenticated())
                 .oauth2Login(oauth -> oauth.defaultSuccessUrl("/home",true));

                   return chainBuilder.build();
    }
}



//Client_ID=Ov23li5zAT5LWEr3n6wT
//Client_secret=c2aa036027f89d2127b16c610a86717df1ec8e47

//            config.userInfoEndpoint(
//                info -> info.userService(new AppUserService())
//            ));
//    return chainBuilder.build();
//  }
//
//}