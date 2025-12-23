package org.hhu.chatgpt.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RoleBasedSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException{
        String targetUrl = "/home";

        if (authentication.getAuthorities().stream()      // iterriert über eine Sammlung von Rollen
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            targetUrl = "/admin";
        }
        else if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_BETREUER"))) {
            targetUrl = "/betreuer";
        }
//        else if (authentication.getAuthorities().stream()
//                .anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT"))) {
//            targetUrl = "/student";
//        }

        response.sendRedirect(targetUrl);   // bei einem Match wird zur angegebenen Seite weitergeleitet
    }
}
