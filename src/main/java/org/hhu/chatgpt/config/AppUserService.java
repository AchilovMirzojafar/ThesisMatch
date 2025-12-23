package org.hhu.chatgpt.config;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashSet;
import java.util.Set;
import java.util.Arrays;
import java.util.stream.Collectors;

@Service
public class AppUserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final DefaultOAuth2UserService defaultService = new DefaultOAuth2UserService();
    private final Set<String> adminLogins; //Speichert Namen der Admins

    public AppUserService(@Value("${security.admins:}") String adminsCsv) {
        this.adminLogins = Arrays.stream(adminsCsv.split(","))
                .map(String::trim) //entfern Whitespaces
                .filter(s -> !s.isEmpty()) // nur nicht-leeere Strings ausgewählt
                .collect(Collectors.toSet()); //speichert Admin-Namen in adminLogins
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("User Service called!");
        OAuth2User originalUser = defaultService.loadUser(userRequest);
        Set<GrantedAuthority> authorities = new HashSet<>(originalUser.getAuthorities());

        authorities.add(new SimpleGrantedAuthority("ROLE_STUDENT"));

        String login = (originalUser.getAttribute("login"));
        if (login != null && adminLogins.contains(login)) { //Prüfung, ob Name in adminLogin enthalten ist
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN")); //Wenn Ja, wird die Rolle Admin zugewiesen
        }

//        if("AchilovMirzojafar".equals(originalUser.getAttribute("login"))){
//            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
//        } else if("MohamedChallou".equals(originalUser.getAttribute("login"))) {
//            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
//        } else if("Marwanakov".equals(originalUser.getAttribute("login"))) {
//            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
//        } else if (("".equals(originalUser.getAttribute("login")))) {
//            authorities.add(new SimpleGrantedAuthority("ROLE_BETREUER"));
//        }

        return new DefaultOAuth2User(authorities, originalUser.getAttributes(), "login");
    }
}
