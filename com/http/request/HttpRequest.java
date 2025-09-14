package com.http.request;

import com.http.common.HttpHeader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * <p>
 * A class which mimics a HTTP request.
 * </p>
 * 
 * <p>
 * This class doesn't have any setters, since once the state
 * has been established, it shouldn't/can't change.
 * </p>
 */
final class HttpRequest {
    private final HttpVerb verb;
    private final String resource;
    private final String httpVersion;
    private final Map<HttpHeader, String> headers;
    private final String body;

    public HttpRequest(HttpVerb verb, String resource, String httpVersion, Map<HttpHeader, String> headers, String body) throws IllegalArgumentException {
        validateVerb(verb);
        this.verb = verb;   

        validateResource(resource);
        this.resource = resource;

        validateHttpVersion(httpVersion);
        this.httpVersion = httpVersion;

        // Copy headers into another map to prevent external modification.
        this.headers = (headers == null) ? Collections.emptyMap() : Map.copyOf(headers);

        validateBody(body, this.verb);
        this.body = body;
    }

    private void validateBody(String body, HttpVerb verb) {
        boolean hasBody = body != null && !body.trim().equals("");
        boolean methodShouldHaveBody = (verb == HttpVerb.POST || verb == HttpVerb.PUT || verb == HttpVerb.PATCH);

        if(hasBody && !methodShouldHaveBody) {
            throw new IllegalArgumentException("HTTP method: " + verb + " can't have a body.");
        }
        
        if(!hasBody && methodShouldHaveBody) {
            throw new IllegalArgumentException("HTTP method: " + verb + " must have a body.");
        }
    }

    private void validateVerb(HttpVerb verb) {
        if(verb == null) {
            throw new IllegalArgumentException("verb must not be null.");
        }
    }

    private void validateHttpVersion(String httpVersion) throws IllegalArgumentException {
        if(httpVersion == null) {
            throw new IllegalArgumentException("HTTP version must not be null.");
        }

        if(httpVersion.trim().equals("")) {
            throw new IllegalArgumentException("HTTP version can't be empty.");
        }
    }
    
    @SuppressWarnings("unused")
    private void validateResource(String resource) throws IllegalArgumentException {
        if(resource == null) {
            throw new IllegalArgumentException("resource must not be null.");
        }

        if(!resource.startsWith("/")) {
            throw new IllegalArgumentException("resource must start with '/'");
        }

        try {
            // Try to create a URI object with the provided resource. 
            // If the resource is invalid, the constructor will throw an error.
            URI uri = new URI(resource);
            // If URI object creation is success, then path is well-formed.
        } catch(URISyntaxException e) {
            // The string is not a valid path according to the rules enforced by the URI constructor.
            throw new IllegalArgumentException("Invalid resource path format: '" + resource + "'" + e);
        }
        
    }

    public HttpVerb getVerb() {
        return this.verb;
    }

    public String getResource() {
        return resource;
    }
    
    public String getHttpVersion() {
        return httpVersion;
    }

    // Since copy of headers is stored, it's safe to return directly.
    public Map<HttpHeader, String> getHeaders() {
        return headers;
    }

    public Optional<String> getBody() {
        return Optional.ofNullable(this.body);
    }

}
