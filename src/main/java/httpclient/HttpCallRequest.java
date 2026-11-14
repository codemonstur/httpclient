package httpclient;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static httpclient.HeaderStrategy.SILENT_REMOVE_NULL_HEADERS;
import static httpclient.HeaderStrategy.THROW_EXCEPTION_ON_NULL;
import static java.net.http.HttpRequest.BodyPublishers.noBody;
import static java.net.http.HttpResponse.BodyHandlers.ofByteArray;

/**
 * The {@code HttpCallRequest} class.
 */
public final class HttpCallRequest {

    private final HttpClient http;
    private final Serializers serializers;
    private final boolean silentRemoveNullHeaders;

    private String scheme = "https";
    private String hostname;
    private int port = -1;
    private String path;
    private String query;
    private String method = "GET";
    private Map<String, String> headers = new HashMap<>();
    private BodyPublisher body;
    private Duration timeout;

    /**
     * Creates a new instance.
     *
     * @param http the http
     */
    public HttpCallRequest(final HttpClient http) {
        this(http, new Serializers.Builder().build(), THROW_EXCEPTION_ON_NULL);
    }

    /**
     * Creates a new instance.
     *
     * @param http the http
     * @param silentRemoveNullHeaders the silent remove null headers
     */
    public HttpCallRequest(final HttpClient http, final HeaderStrategy silentRemoveNullHeaders) {
        this(http, new Serializers.Builder().build(), silentRemoveNullHeaders);
    }

    /**
     * Creates a new instance.
     *
     * @param http the http
     * @param serializers the serializers
     * @param headerStrategy the header strategy
     */
    public HttpCallRequest(final HttpClient http, final Serializers serializers, final HeaderStrategy headerStrategy) {
        this.http = http;
        this.serializers = serializers;
        this.silentRemoveNullHeaders = headerStrategy == SILENT_REMOVE_NULL_HEADERS;
    }

    /**
     * Returns the result of {@code scheme}.
     *
     * @param scheme the scheme
     * @return the result
     */
    public HttpCallRequest scheme(final String scheme) {
        this.scheme = scheme;
        return this;
    }
    /**
     * Returns the result of {@code hostname}.
     *
     * @param hostname the hostname
     * @return the result
     */
    public HttpCallRequest hostname(final String hostname) {
        this.hostname = hostname;
        return this;
    }
    /**
     * Returns the result of {@code port}.
     *
     * @param port the port
     * @return the result
     */
    public HttpCallRequest port(final int port) {
        this.port = port;
        return this;
    }
    /**
     * Returns the result of {@code uri}.
     *
     * @param uri the uri
     * @return the result
     */
    public HttpCallRequest uri(final String uri) {
        return uri(URI.create(uri));
    }
    /**
     * Returns the result of {@code uri}.
     *
     * @param uri the uri
     * @return the result
     */
    public HttpCallRequest uri(final URI uri) {
        this.scheme = uri.getScheme();
        this.hostname = uri.getHost();
        this.port = uri.getPort();
        // raw variants: the path/query must reach the server exactly as written,
        // decoding here would make the client double-encode on send
        this.path = uri.getRawPath();
        this.query = uri.getRawQuery();
        return this;
    }
    /**
     * Returns the result of {@code method}.
     *
     * @param method the method
     * @return the result
     */
    public HttpCallRequest method(final String method) {
        this.method = method;
        return this;
    }
    /**
     * Returns the result of {@code method}.
     *
     * @param method the method
     * @param path the path
     * @return the result
     */
    public HttpCallRequest method(final String method, final String path) {
        this.method = method;
        this.path = path;
        return this;
    }
    /**
     * Returns the result of {@code get}.
     *
     * @return the result
     */
    public HttpCallRequest get() {
        this.method = "GET";
        return this;
    }
    /**
     * Returns the result of {@code get}.
     *
     * @param path the path
     * @return the result
     */
    public HttpCallRequest get(final String path) {
        this.method = "GET";
        this.path = path;
        return this;
    }
    /**
     * Returns the result of {@code delete}.
     *
     * @return the result
     */
    public HttpCallRequest delete() {
        this.method = "DELETE";
        return this;
    }
    /**
     * Returns the result of {@code delete}.
     *
     * @param path the path
     * @return the result
     */
    public HttpCallRequest delete(final String path) {
        this.method = "DELETE";
        this.path = path;
        return this;
    }
    /**
     * Returns the result of {@code head}.
     *
     * @return the result
     */
    public HttpCallRequest head() {
        this.method = "HEAD";
        return this;
    }
    /**
     * Returns the result of {@code head}.
     *
     * @param path the path
     * @return the result
     */
    public HttpCallRequest head(final String path) {
        this.method = "HEAD";
        this.path = path;
        return this;
    }
    /**
     * Returns the result of {@code post}.
     *
     * @return the result
     */
    public HttpCallRequest post() {
        this.method = "POST";
        return this;
    }
    /**
     * Returns the result of {@code post}.
     *
     * @param path the path
     * @return the result
     */
    public HttpCallRequest post(final String path) {
        this.method = "POST";
        this.path = path;
        return this;
    }
    /**
     * Returns the result of {@code put}.
     *
     * @return the result
     */
    public HttpCallRequest put() {
        this.method = "PUT";
        return this;
    }
    /**
     * Returns the result of {@code put}.
     *
     * @param path the path
     * @return the result
     */
    public HttpCallRequest put(final String path) {
        this.method = "PUT";
        this.path = path;
        return this;
    }
    /**
     * Returns the result of {@code patch}.
     *
     * @return the result
     */
    public HttpCallRequest patch() {
        this.method = "PATCH";
        return this;
    }
    /**
     * Returns the result of {@code patch}.
     *
     * @param path the path
     * @return the result
     */
    public HttpCallRequest patch(final String path) {
        this.method = "PATCH";
        this.path = path;
        return this;
    }
    /**
     * Returns the result of {@code header}.
     *
     * @param name the name
     * @param value the value
     * @return the result
     */
    public HttpCallRequest header(final String name, final String value) {
        if (name == null || name.isEmpty()) {
            if (silentRemoveNullHeaders) return this;
            else throw new IllegalArgumentException("Header name is null or empty");
        }
        if (value == null) {
            if (silentRemoveNullHeaders) return this;
            else throw new IllegalArgumentException("Header value for '" + name + "' is null");
        }
        // header names are case-insensitive: replace any casing variant already set
        this.headers.keySet().removeIf(existing -> existing.equalsIgnoreCase(name));
        this.headers.put(name, value);
        return this;
    }
    /**
     * Returns the result of {@code contentType}.
     *
     * @param type the type
     * @return the result
     */
    public HttpCallRequest contentType(final String type) {
        return header("Content-Type", type);
    }
    /**
     * Returns the result of {@code timeout}.
     *
     * @param timeout the timeout
     * @return the result
     */
    public HttpCallRequest timeout(final Duration timeout) {
        this.timeout = timeout;
        return this;
    }
    /**
     * Returns the result of {@code body}.
     *
     * @param body the body
     * @return the result
     */
    public HttpCallRequest body(final String body) {
        return body(HttpRequest.BodyPublishers.ofString(body));
    }
    /**
     * Returns the result of {@code body}.
     *
     * @param body the body
     * @return the result
     */
    public HttpCallRequest body(final BodyPublisher body) {
        this.body = body;
        return this;
    }
    /**
     * Returns the result of {@code body}.
     *
     * @param requestBody the request body
     * @return the result
     */
    public HttpCallRequest body(final RequestBody requestBody) {
        // last call wins: the body's own content-type carries the multipart
        // boundary, an earlier contentType() must never discard it
        header("Content-Type", requestBody.getHeader());
        this.body = requestBody.build();
        return this;
    }

    /**
     * Returns the result of {@code execute}.
     *
     * @return the result
     * @throws IOException if the operation fails
     */
    public HttpCallResponse execute() throws IOException {
        return new HttpCallResponse(serializers, send(ofByteArray()));
    }

    /**
     * Returns the result of {@code send}.
     *
     * @param <U> the type parameter
     * @param handler the handler
     * @return the result
     * @throws IOException if the operation fails
     */
    public <U> HttpResponse<U> send(final BodyHandler<U> handler) throws IOException {
        try {
            final var builder = HttpRequest.newBuilder()
                .uri(URI.create(scheme + "://" + hostname + getPort() + getPath() + getQuery()))
                .method(method, body == null ? noBody() : body);
            if (timeout != null) builder.timeout(timeout);
            for (final var entry : headers.entrySet())
                builder.header(entry.getKey(), entry.getValue());
            return http.send(builder.build(), handler);
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Interrupted during IO", e);
        }
    }

    private String getPort() {
        return port == -1 ? "" : ":" + port;
    }
    private String getPath() {
        if (path == null || path.isEmpty()) return "";
        return path.startsWith("/") ? path : "/" + path;
    }
    private String getQuery() {
        return query == null || query.isEmpty() ? "" : "?" + query;
    }

}
