package com.http.core;

import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private int port;

    public HttpServer(int port) {
        this.port = port;
    }

    public void start() {
        
    }

    public void stop() {

    }
}
