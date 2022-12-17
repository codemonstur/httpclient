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
import static java.net.http.HttpResponse.BodyHandlers.ofString;

public class HttpCallRequest<T extends HttpCallRequest<T>> {

    private final HttpClient http;
    private String scheme = "https";
    private String hostname;
    private int port = -1;
    private String path;
    private String method = "GET";
    private Map<String, String> headers = new HashMap<>();
    private BodyPublisher body;

    public HttpCallRequest(final HttpClient http) {
        this.http = http;
    }

    public T scheme(final String scheme) {
        this.scheme = scheme;
        return (T) this;
    }
    public T hostname(final String hostname) {
        this.hostname = hostname;
        return (T) this;
    }
    public T port(final int port) {
        this.port = port;
        return (T) this;
    }
    public T uri(final String uri) {
        return uri(URI.create(uri));
    }
    public T uri(final URI uri) {
        this.scheme = uri.getScheme();
        this.hostname = uri.getHost();
        this.port = uri.getPort();
        this.path = uri.getPath();
        return (T) this;
    }
    public T method(final String method, final String path) {
        this.method = method;
        this.path = path;
        return (T) this;
    }
    public T get(final String path) {
        this.method = "GET";
        this.path = path;
        return (T) this;
    }
    public T delete(final String path) {
        this.method = "DELETE";
        this.path = path;
        return (T) this;
    }
    public T head(final String path) {
        this.method = "HEAD";
        this.path = path;
        return (T) this;
    }
    public T post(final String path) {
        this.method = "POST";
        this.path = path;
        return (T) this;
    }
    public T put(final String path) {
        this.method = "PUT";
        this.path = path;
        return (T) this;
    }
    public T patch(final String path) {
        this.method = "PATCH";
        this.path = path;
        return (T) this;
    }
    public T header(final String name, final String value) {
        this.headers.put(name, value);
        return (T) this;
    }
    public T contentType(final String type) {
        this.headers.put("Content-Type", type);
        return (T) this;
    }
    public T body(final BodyPublisher body) {
        this.body = body;
        return (T) this;
    }
    public T body(final RequestBody requestBody) {
        this.headers.putIfAbsent("Content-Type", requestBody.getHeader());
        this.body = requestBody.build();
        return (T) this;
    }

    public HttpCallResponse execute() throws IOException {
        return new HttpCallResponse(send(ofString()));
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
