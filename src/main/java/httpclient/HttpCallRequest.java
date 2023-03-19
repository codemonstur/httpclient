package httpclient;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.util.HashMap;
import java.util.Map;

import static java.net.http.HttpRequest.BodyPublishers.noBody;
import static java.net.http.HttpResponse.BodyHandlers.ofByteArray;
import static java.net.http.HttpResponse.BodyHandlers.ofString;

public final class HttpCallRequest {

    private final HttpClient http;
    private final Serializers serializers;

    private String scheme = "https";
    private String hostname;
    private int port = -1;
    private String path;
    private String method = "GET";
    private Map<String, String> headers = new HashMap<>();
    private BodyPublisher body;

    public HttpCallRequest(final HttpClient http, final Serializers serializers) {
        this.http = http;
        this.serializers = serializers;
    }

    public HttpCallRequest scheme(final String scheme) {
        this.scheme = scheme;
        return this;
    }
    public HttpCallRequest hostname(final String hostname) {
        this.hostname = hostname;
        return this;
    }
    public HttpCallRequest port(final int port) {
        this.port = port;
        return this;
    }
    public HttpCallRequest uri(final String uri) {
        return uri(URI.create(uri));
    }
    public HttpCallRequest uri(final URI uri) {
        this.scheme = uri.getScheme();
        this.hostname = uri.getHost();
        this.port = uri.getPort();
        this.path = uri.getPath();
        return this;
    }
    public HttpCallRequest method(final String method, final String path) {
        this.method = method;
        this.path = path;
        return this;
    }
    public HttpCallRequest get(final String path) {
        this.method = "GET";
        this.path = path;
        return this;
    }
    public HttpCallRequest delete(final String path) {
        this.method = "DELETE";
        this.path = path;
        return this;
    }
    public HttpCallRequest head(final String path) {
        this.method = "HEAD";
        this.path = path;
        return this;
    }
    public HttpCallRequest post(final String path) {
        this.method = "POST";
        this.path = path;
        return this;
    }
    public HttpCallRequest put(final String path) {
        this.method = "PUT";
        this.path = path;
        return this;
    }
    public HttpCallRequest patch(final String path) {
        this.method = "PATCH";
        this.path = path;
        return this;
    }
    public HttpCallRequest header(final String name, final String value) {
        this.headers.put(name, value);
        return this;
    }
    public HttpCallRequest contentType(final String type) {
        this.headers.put("Content-Type", type);
        return this;
    }
    public HttpCallRequest body(final String body) {
        return body(HttpRequest.BodyPublishers.ofString(body));
    }
    public HttpCallRequest body(final BodyPublisher body) {
        this.body = body;
        return this;
    }
    public HttpCallRequest body(final RequestBody requestBody) {
        this.headers.putIfAbsent("Content-Type", requestBody.getHeader());
        this.body = requestBody.build();
        return this;
    }

    public HttpCallResponse execute() throws IOException {
        return new HttpCallResponse(serializers, send(ofByteArray()));
    }

    public <U> HttpResponse<U> send(final BodyHandler<U> handler) throws IOException {
        try {
            final var uri = URI.create(scheme + "://" + hostname + getPort() + path);
            final var request = HttpRequest.newBuilder()
                .uri(uri).method(method, body == null ? noBody() : body);
            for (final var entry : headers.entrySet())
                request.header(entry.getKey(), entry.getValue());
            return http.send(request.build(), handler);
        } catch (final InterruptedException e) {
            throw new IOException("Interrupted during IO", e);
        }
    }

    private String getPort() {
        return port == -1 ? "" : ":" + port;
    }

}
