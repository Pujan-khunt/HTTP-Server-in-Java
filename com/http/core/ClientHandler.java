package com.http.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.http.request.HttpRequest;
import com.http.common.Delimiter;
import com.http.common.HttpHeader;
import com.http.common.InvalidHttpRequestException;
import com.http.request.HttpRequestParser;
import com.http.response.HttpResponse;
import com.http.response.HttpStatus;

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

            // Received the entire HTTP request.
            StringBuilder requestBuilder = new StringBuilder();
            String httpLine;

            // Read status line and headers.
            while ((httpLine = socketReader.readLine()) != null && !httpLine.isEmpty()) {
                if (httpLine.isEmpty()) {
                    break;
                }

                requestBuilder.append(httpLine).append(Delimiter.HttpRequestLineDelimiter.getDelimiterValue());
            }

            // Read body for request with specific HTTP verbs.
            // ...
            // No request, just close it.
            String unparsedHttpRequest = requestBuilder.toString();
            if (unparsedHttpRequest.isEmpty()) {
                return;
            }

            HttpResponse httpResponse;
            try {
                // parse the raw http request
                HttpRequestParser httpRequestParser = new HttpRequestParser();
                HttpRequest httpRequest = httpRequestParser.parse(unparsedHttpRequest);

                // Debug
                System.out.println("Received: " + httpRequest.getVerb() + " " + httpRequest.getResource());

                // Handle request using Server Side Logic.
                httpResponse = handleRequest(httpRequest);
            } catch (InvalidHttpRequestException e) {
                // Debug
                System.out.println("Failed to parse HTTP request: " + e.getMessage());

                // Since parsing is failed, send a 400 BAD REQUEST response.
                String responseBody = e.getMessage();
                httpResponse = new HttpResponse.Builder(HttpStatus.BAD_REQUEST_400)
											.header(HttpHeader.Content_Type, "text/plain")
											.header(HttpHeader.Content_Length, Integer.toString(responseBody.length()))
											.body(responseBody)
											.build();
            }

            socketWriter.close();
            socketReader.close();
        } catch (IOException e) {
            System.out.println("Client disconnected due to I/O error. Client Info: " + clientSocket.getInetAddress());
        }
    }

    private HttpResponse handleRequest(HttpRequest httpRequest) {
		return null;
    }
}
