package com.http.core;

import com.http.common.Delimiter;
import com.http.common.HttpHeader;
import com.http.common.InvalidHttpRequestException;
import com.http.request.HttpRequest;
import com.http.request.HttpRequestParser;
import com.http.request.HttpVerb;
import com.http.response.HttpResponse;
import com.http.response.HttpStatus;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;

public class ClientHandler implements Runnable {

    private final Socket clientSocket;
    private final Router router;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;

        this.router = new Router();
        configureRouter(this.router);
    }

    private void configureRouter(Router router) {
        // Home Route.
        router.addRoute(HttpVerb.GET, "/", (_) -> {
            return new HttpResponse.Builder(HttpStatus.OK_200)
                    .header(HttpHeader.Content_Type, "text/html")
                    .body("<html><body><h1>You are currently at the Home Route</h1></body></html>")
                    .build();
        });
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

        } catch (IOException e) {
            System.out.println("Client disconnected due to I/O error. Client Info: " + clientSocket.getInetAddress());
        } finally {
            try {
                clientSocket.close();
            } catch(IOException e) {
                System.out.println("Error while closing socket");
            }
        }
    }

    // Sending response to the underlying socket after serializing the response.
    private void sendResponse(PrintWriter writer, OutputStream outputStream, HttpResponse response) throws IOException {
        String serializedStatusLine = response.getHttpVersion() + " " + response.getStatus().getStatusCode() + " " + response.getStatus().getStatusMessage();
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
