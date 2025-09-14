package com.http;

import com.http.app.Application;
import com.http.routing.Router;
import com.http.server.HttpServer;

public class Main {
    private static final int PORT = 8080;

    public static void main(String[] args) {
        Application app = new Application();

        Router router = app.configureRouter();

        HttpServer server = new HttpServer(PORT, router);

        server.start();
    }
}
