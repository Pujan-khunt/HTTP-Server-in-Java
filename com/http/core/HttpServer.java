package com.http.core;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class HttpServer {
    private final int port;
    private ServerSocket serverSocket;
    private Socket clientSocket;

    public HttpServer(int port) {
        this.port = port;
    }

    public void start() {
        System.out.println("Server about to start.");

        try {
            serverSocket = new ServerSocket(this.port);
            System.out.println("Server now listening for clients on port = " + this.port);
            
            while(true) {
                clientSocket = serverSocket.accept();
                Thread handleClient = new Thread(new ClientHandler(clientSocket));
                handleClient.start();
            }
        } catch (IOException e) {
            System.out.println("Server error occured due to I/O error. " + e.getMessage());
        }

        System.out.println("Server shutting down.");
    }
}
