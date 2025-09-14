package com.http.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.http.routing.Router;

public class HttpServer {

    private final int port;
    private final Router router;

    public HttpServer(int port, Router router) {
        this.port = port;
        this.router = router;
    }

    public void start() {
        System.out.println("Server about to start...");

        try (
            ServerSocket serverSocket = new ServerSocket(this.port)
        ) {
            System.out.println("Server now listening for clients on port = " + this.port);

            while (true) {
                // Receive from Socket object after client connection.
                Socket clientSocket = serverSocket.accept();

                // Assign new thread for handling client.
                Thread handleClient = new Thread(new ClientConnection(clientSocket, router));

                // Run the separate thread from the main thread.
                handleClient.start();
            }
        } catch (IOException e) {
            System.out.println("Server error occured due to I/O error. " + e.getMessage());
        }

        System.out.println("Server shutting down...");
    }
}
