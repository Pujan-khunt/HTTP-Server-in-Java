package com.http.request;

import com.http.common.HttpHeader;
import com.http.common.InvalidHttpRequestException;
import com.http.common.Delimiter;

import java.util.Map;

public class HttpRequestParser {
    private HttpVerb verb;
    private String resource;
    private String httpVersion;
    private Map<HttpHeader, String> headers;
    private String body;

    public HttpRequest parse(String httpRequest) throws InvalidHttpRequestException {
        String[] httpRequestLines = httpRequest.split(Delimiter.HttpRequestLineDelimiter.getDelimiterValue());
        String statusLine = httpRequestLines[0];
        String[] statuses = statusLine.split(Delimiter.HttpRequestStatusDelimiter.getDelimiterValue());

        // Parsing HTTP Verb.
        switch(statuses[0]) {
            case "GET" -> verb = HttpVerb.GET;
            case "POST" -> verb = HttpVerb.POST;
            case "PUT" -> verb = HttpVerb.PUT;
            case "PATCH" -> verb = HttpVerb.PATCH;
            case "DELETE" -> verb = HttpVerb.DELETE;
            case "HEAD" -> verb = HttpVerb.HEAD;
            case "OPTIONS" -> verb = HttpVerb.OPTIONS;
            default -> throw new IllegalArgumentException("Invalid HTTP verb: " + statuses[0]);
        }

        // Parsing HTTP Resource.
        this.resource = statuses[1];

        // Parsing HTTP Version.
        this.httpVersion = statuses[2];

        boolean containsBody = verb == HttpVerb.POST || verb == HttpVerb.PUT || verb == HttpVerb.PATCH;
        int idxOfEmptyLineBetweenHeadersAndBody = -1;
        
        if(containsBody) {
            // Find index of line separating headers and body.
            for(int i = 1; i < httpRequestLines.length; i++) {
                if(httpRequestLines[i].trim().equalsIgnoreCase(Delimiter.HttpRequestLineDelimiter.getDelimiterValue())) {
                    idxOfEmptyLineBetweenHeadersAndBody = i;
                    break;
                }
            }

            if(idxOfEmptyLineBetweenHeadersAndBody == -1) {
                throw new InvalidHttpRequestException("No valid separator between headers and body.");
            }

            // Parsing the HTTP body.
            for(int i = idxOfEmptyLineBetweenHeadersAndBody; i < httpRequestLines.length; i++) {
                body += httpRequestLines[i];
            }
        } else {
            idxOfEmptyLineBetweenHeadersAndBody = httpRequestLines.length;
        }

        // Headers lie in the range [1, idx-1]
        for(int i = 1; i < idxOfEmptyLineBetweenHeadersAndBody; i++) {
            String[] unparsedHeader = httpRequestLines[i].split(Delimiter.HttpHeaderDelimiter.getDelimiterValue());
            HttpHeader header;
            String headerValue;

            // Parsing each request header
            switch(unparsedHeader[0].toLowerCase()) {
                case "Host" -> header = HttpHeader.Host;
                case "User-Agent" -> header = HttpHeader.User_Agent;
                case "Accept" -> header = HttpHeader.Accept;
                case "Accept-Language" -> header = HttpHeader.Accept_Language;
                case "Accept-Encoding" -> header = HttpHeader.Accept_Encoding;
                case "Content-Encoding" -> header = HttpHeader.Content_Encoding;
                case "Server" -> header = HttpHeader.Server;
                case "Date" -> header = HttpHeader.Date;
                case "Content-Type" -> header = HttpHeader.Content_Type;
                case "Content-Length" -> header = HttpHeader.Content_Length;
                case "Location" -> header = HttpHeader.Location;
                case "Connection" -> header = HttpHeader.Connection;
                default -> throw new InvalidHttpRequestException("Invalid HTTP header: " + unparsedHeader[0]);
            }

            // Parsing each header value.
            headerValue = unparsedHeader[1].trim();

            headers.put(header, headerValue);
        }
        
        HttpRequest parsedHttpRequest = new HttpRequest(verb, resource, httpVersion, headers, body);
        return parsedHttpRequest;
    }
}
