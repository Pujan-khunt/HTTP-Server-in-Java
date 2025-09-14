package com.http.routing;

import com.http.model.request.HttpRequest;
import com.http.model.response.HttpResponse;

@FunctionalInterface
public interface RouteHandler {
    HttpResponse handle(HttpRequest request);
}
