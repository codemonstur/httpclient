package httpclient;

import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class FakeHttpResponse implements HttpResponse<byte[]> {
    private final int statusCode;
    private final byte[] body;
    private final Map<String, List<String>> headers;
    private final java.net.URI uri;

    public FakeHttpResponse(int statusCode, byte[] body, Map<String, List<String>> headers, java.net.URI uri) {
        this.statusCode = statusCode;
        this.body = body;
        this.headers = headers;
        this.uri = uri;
    }

    @Override public int statusCode() { return statusCode; }
    @Override public java.net.http.HttpRequest request() { return null; }
    @Override public Optional<HttpResponse<byte[]>> previousResponse() { return Optional.empty(); }
    @Override public java.net.http.HttpHeaders headers() {
        return java.net.http.HttpHeaders.of(headers, (a, b) -> true);
    }
    @Override public byte[] body() { return body; }
    @Override public Optional<javax.net.ssl.SSLSession> sslSession() { return Optional.empty(); }
    @Override public java.net.URI uri() { return uri; }
    @Override public java.net.http.HttpClient.Version version() { return java.net.http.HttpClient.Version.HTTP_1_1; }
}
