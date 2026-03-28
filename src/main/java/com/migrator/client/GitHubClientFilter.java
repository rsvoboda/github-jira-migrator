package com.migrator.client;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.IOException;
import java.util.Optional;

@Provider
public class GitHubClientFilter implements ClientRequestFilter {

    @ConfigProperty(name = "github.api.token")
    Optional<String> githubToken;

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        requestContext.getHeaders().add(HttpHeaders.ACCEPT, "application/vnd.github.v3+json");
        
        githubToken.ifPresent(token -> {
            if (!token.isEmpty()) {
                requestContext.getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
            }
        });
    }
}
