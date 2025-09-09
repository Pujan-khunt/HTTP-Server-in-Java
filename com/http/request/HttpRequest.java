package com.http.request;

import java.util.Map;

import com.http.common.HttpHeader;

class HttpRequest {
    private HttpMethod method;
    private String resource;
    private String httpVersion;
    private Map<HttpHeader, String> headers;
    private String body;
}
