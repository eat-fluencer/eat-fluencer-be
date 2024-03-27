package com.eatfluencer.eatfluencer.auth;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

@Component
public class OAuth2ServiceFactory {

    private final Map<String, OAuth2Service> oauthServices;

    public OAuth2ServiceFactory(List<OAuth2Service> services) {
        oauthServices = new HashMap<>();
        services.forEach(service ->
            oauthServices.put(service.getProvider().toLowerCase(), service));
    }

    public OAuth2Service getService(String provider) {
        return Optional.ofNullable(oauthServices.get(provider.toLowerCase()))
                	   .orElseThrow(() -> new IllegalArgumentException("Invalid provider: " + provider));
    }
    
}
