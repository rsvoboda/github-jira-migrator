package com.migrator.client;

import com.migrator.model.github.GitHubIssue;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

@Path("/repos")
@RegisterRestClient(configKey = "github-api")
@RegisterProvider(GitHubClientFilter.class)
@ApplicationScoped
public interface GitHubClient {

    @GET
    @Path("/{owner}/{repo}/issues/{issueNumber}")
    @Produces(MediaType.APPLICATION_JSON)
    GitHubIssue getIssue(
            @PathParam("owner") String owner,
            @PathParam("repo") String repo,
            @PathParam("issueNumber") int issueNumber
    );

    @GET
    @Path("/{owner}/{repo}/issues")
    @Produces(MediaType.APPLICATION_JSON)
    List<GitHubIssue> getIssues(
            @PathParam("owner") String owner,
            @PathParam("repo") String repo,
            @QueryParam("state") String state,
            @QueryParam("labels") String labels,
            @QueryParam("per_page") int perPage,
            @QueryParam("page") int page
    );
}
