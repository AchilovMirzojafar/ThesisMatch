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
    private final Set<Integer> adminLogins; //Speichert ID der Admins

    public AppUserService(@Value("${security.admins:}") String adminsCsv) {
        this.adminLogins = Arrays.stream(adminsCsv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Integer::valueOf)
                .collect(Collectors.toSet());
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("User Service called!");
        OAuth2User originalUser = defaultService.loadUser(userRequest);
        Set<GrantedAuthority> authorities = new HashSet<>(originalUser.getAuthorities());

        authorities.add(new SimpleGrantedAuthority("ROLE_STUDENT"));

        Integer id = (originalUser.getAttribute("id"));
        if (id != null && adminLogins.contains(id)) { //Prüfung, ob Name in adminId enthalten ist
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN")); //Wenn Ja, wird die Rolle Admin zugewiesen
        }
        else if (("".equals(originalUser.getAttribute("id")))) {
            authorities.add(new SimpleGrantedAuthority("ROLE_BETREUER"));
        }

            return new DefaultOAuth2User(authorities, originalUser.getAttributes(), "id");
        }
    }
