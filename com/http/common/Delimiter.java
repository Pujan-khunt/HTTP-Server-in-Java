package com.http.common;

public enum Delimiter {
    HttpRequestLineDelimiter("\r\n"),
    HttpRequestStatusDelimiter(" "),
    HttpHeaderDelimiter(": ");

    private final String delimiterValue;

    private Delimiter(String delimiterValue) {
        this.delimiterValue = delimiterValue;
    }

    public String getDelimiterValue() {
        return this.delimiterValue;
    }
}
