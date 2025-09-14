package com.http.routing;

import java.util.HashMap;
import java.util.Map;

import com.http.model.common.HttpHeader;
import com.http.model.request.HttpRequest;
import com.http.model.request.HttpVerb;
import com.http.model.response.HttpResponse;
import com.http.model.response.HttpStatus;

public class Router {

    // Stores all registerd routes.
    // Key: "GET /route"
    // Value: Handler Function
    private final Map<String, RouteHandler> routes = new HashMap<>();

    /**
     * Registers a handler for a specific HTTP Verb and a resource path.
     */
    public void addRoute(HttpVerb verb, String resource, RouteHandler handler) {
        String routeKey = verb.toString() + " " + resource;
        this.routes.put(routeKey, handler);
    }

    public HttpResponse route(HttpRequest request) {
        String routeKey = request.getVerb().toString() + " " + request.getResource();
        RouteHandler handler = routes.get(routeKey);

        // If no handler exists for a route key, then send a 404 NOT FOUND response.
        if (handler == null) {
            System.out.println("No Route Handler found for route key: " + routeKey);

            return new HttpResponse.Builder(HttpStatus.NOT_FOUND_404)
                .header(HttpHeader.Content_Type, "text/plain")
                .body("Invalid Route.")
                .build();
        }

        return handler.handle(request);
    }

}
