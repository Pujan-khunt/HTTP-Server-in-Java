package com.http.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            BufferedReader socketReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter socketWriter = new PrintWriter(clientSocket.getOutputStream(), true);

            // Received the entire 
            String httpRequest = socketReader.readLine();

            socketWriter.close();
            socketReader.close();
        } catch(IOException e) {
            System.out.println("Client disconnected due to I/O error. Client Info: " + clientSocket.getInetAddress());
        }
    }

}
