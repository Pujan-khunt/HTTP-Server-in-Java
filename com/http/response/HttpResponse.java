package com.http.response;

import com.http.common.HttpHeader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {

    private HttpStatus status;
    private Map<HttpHeader, String> headers;
    private String httpVersion;

    private HttpResponse(Builder builder) {
        this.status = builder.status;
        this.headers = builder.headers;
        this.httpVersion = "HTTP/1.1";
    }

    public HttpStatus getStatus() {
        return status;
    }

    public Map<HttpHeader, String> getHeaders() {
        return headers;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public static class Builder {

        private HttpStatus status;
        private Map<HttpHeader, String> headers;
        private byte[] body;

        public Builder(HttpStatus status) {
            this.status = status;
            this.headers = new HashMap<>();
        }

        public Builder header(HttpHeader header, String headerValue) {
            this.headers.put(header, headerValue);
            return this;
        }

        public Builder body(String textBody, Charset charset) {
            this.body = textBody.getBytes(charset);
            return this;
        }

        public Builder body(String textBody) {
            this.body(textBody, StandardCharsets.UTF_8);
            return this;
        }

        public Builder body(byte[] byteBody) {
            this.body = byteBody;
            return this;
        }

        public HttpResponse build() {
            // Automatically add required headers.
            this.header(HttpHeader.Date, new Date().toString());
            this.header(HttpHeader.Server, "My Custom HTTP Java Server v0.0");

            // Create "Content-Length" header based on body.
            if (this.body != null) {
                this.header(HttpHeader.Content_Length, String.valueOf(this.body.length));
            } else {
                this.header(HttpHeader.Content_Length, "0");
            }

            return new HttpResponse(this);
        }

    }
}
