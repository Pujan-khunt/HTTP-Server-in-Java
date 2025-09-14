package com.http.app;

import com.http.model.common.HttpHeader;
import com.http.model.request.HttpVerb;
import com.http.model.response.HttpResponse;
import com.http.model.response.HttpStatus;
import com.http.routing.Router;

public class Application {

    /**
     * Defines Application Routes.
     */
    public Router configureRouter() {
        Router router = new Router();

        // Home Route returning html.
        router.addRoute(HttpVerb.GET, "/", (_) -> {
            return new HttpResponse.Builder(HttpStatus.OK_200)
                .header(HttpHeader.Content_Type, "text/html")
                .body("<html><body><h1>You are currently at the Home Route</h1></body></html>")
                .build();
        });

        // Route returning json.
        router.addRoute(HttpVerb.GET, "/json", (_) -> {
            return new HttpResponse.Builder(HttpStatus.OK_200)
                .header(HttpHeader.Content_Type, "application/json")
                .body("{ \"name\": \"Pujan Khunt\", \"age\": 18 }")
                .build();
        });

        return router;
    }
}
