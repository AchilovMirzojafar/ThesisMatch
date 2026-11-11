package org.hhu.thesismatch.config;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.hhu.thesismatch.application.applicationservice.BetreuerReadService;
import org.hhu.thesismatch.domain.model.GitHubId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class AppUserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final DefaultOAuth2UserService defaultService = new DefaultOAuth2UserService();
    private final Set<Integer> adminLogins;
    private final BetreuerReadService betreuerReadService;

    public AppUserService(@Value("${security.admins:}") String adminsCsv,
                          BetreuerReadService betreuerReadService) {
        this.adminLogins = Arrays.stream(adminsCsv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Integer::valueOf)
                .collect(Collectors.toSet());
        this.betreuerReadService = betreuerReadService;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User originalUser = defaultService.loadUser(userRequest);
        Set<GrantedAuthority> authorities = new HashSet<>(originalUser.getAuthorities());

        Integer id = originalUser.getAttribute("id");

        // default role
        authorities.add(new SimpleGrantedAuthority("ROLE_STUDENT"));

        // admin?
        if (id != null && adminLogins.contains(id)) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }

        // betreuer? (exists in DB)
        if (id != null && id > 0 && betreuerReadService.findProfile(new GitHubId(id)).isPresent()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_BETREUER"));
        }

        return new DefaultOAuth2User(authorities, originalUser.getAttributes(), "id");
    }
}
