package com.http.server;

import com.http.model.common.Delimiter;
import com.http.model.common.HttpHeader;
import com.http.exception.InvalidHttpRequestException;
import com.http.protocol.HttpRequestParser;
import com.http.model.request.HttpRequest;
import com.http.model.response.HttpResponse;
import com.http.model.response.HttpStatus;
import com.http.routing.Router;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;

public class ClientConnection implements Runnable {

    private final Socket clientSocket;
    private final Router router;

    public ClientConnection(Socket clientSocket, Router router) {
        this.clientSocket = clientSocket;
        this.router = router;
    }

    @Override
    public void run() {
        try (
            BufferedReader socketReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            OutputStream socketOutputStream = clientSocket.getOutputStream();
            PrintWriter socketWriter = new PrintWriter(socketOutputStream, true);
        ) {
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

                // Use custom router to handle requests.
                httpResponse = this.router.route(httpRequest);
            } catch (InvalidHttpRequestException e) {
                // Debug
                System.out.println("400_BAD_REQUEST. Failed to parse HTTP request: " + e.getMessage());

                // Since parsing is failed, send a 400 BAD REQUEST response.
                String responseBody = e.getMessage();
                httpResponse = new HttpResponse.Builder(HttpStatus.BAD_REQUEST_400)
                    .header(HttpHeader.Content_Type, "text/plain")
                    .header(HttpHeader.Content_Length, Integer.toString(responseBody.length()))
                    .body(responseBody)
                    .build();
            }

            sendResponse(socketWriter, socketOutputStream, httpResponse);

        } catch (IOException e) {
            System.out.println("Client disconnected due to I/O error. Client Info: " + clientSocket.getInetAddress());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.out.println("Error while closing socket");
            }
        }
    }

    // Sending response to the underlying socket after serializing the response.
    private void sendResponse(PrintWriter writer, OutputStream outputStream, HttpResponse response) throws IOException {
        String serializedStatusLine = response.getHttpVersion() + " " + response.getStatus().getStatusCode() + " "
            + response.getStatus().getStatusMessage();
        writer.println(serializedStatusLine);

        for (Map.Entry<HttpHeader, String> header : response.getHeaders().entrySet()) {
            String serializedHeaderValue = header.getKey().toString().toLowerCase() + ": " + header.getValue();
            writer.println(serializedHeaderValue);
        }

        // Ensuring empty line between body and headers.
        writer.println();

        // Writing body after null checking.
        if (response.getBody() != null) {
            outputStream.write(response.getBody());
            outputStream.flush();
        }
    }
}
