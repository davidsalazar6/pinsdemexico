package com.pins.ordermanagement.security;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Component
public class AuthSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response) {
        // Get the role of logged in user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String role = auth.getAuthorities().toString();

        String targetUrl = "";
        if (role.contains("USER")) {
            targetUrl = "/customer";
        } else if (role.contains("ADMIN")) {
            targetUrl = "/admin";
        }
        return targetUrl;
    }

    public String redirectIfAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || AnonymousAuthenticationToken.class.
                isAssignableFrom(authentication.getClass())) {
            return null;
        }
        String role = authentication.getAuthorities().toString();
        String targetUrl = null;
        if (role.contains("USER")) {
            targetUrl = "redirect:customer";
        } else if (role.contains("ADMIN")) {
            targetUrl = "redirect:admin";
        }
        return targetUrl;
    }


    public MyUserPrincipal getLoginInUserPrincipal() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || AnonymousAuthenticationToken.class.
                isAssignableFrom(auth.getClass())) {
            return null;
        }
        MyUserPrincipal myUserPrincipal = (MyUserPrincipal)auth.getPrincipal();
        if(myUserPrincipal==null || myUserPrincipal.getUsername().equals("")){
            return null;
        }
        return myUserPrincipal;
    }
}