package com.http.core;

import com.http.request.HttpRequest;
import com.http.response.HttpResponse;

@FunctionalInterface
public interface RouteHandler {
    HttpResponse handle(HttpRequest request);
}
