package httpclient;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class FakeHttpClient extends HttpClient {
    public HttpRequest lastRequest;
    public HttpResponse<byte[]> cannedResponse;

    public FakeHttpClient(HttpResponse<byte[]> cannedResponse) {
        this.cannedResponse = cannedResponse;
    }

    @Override
    public java.net.http.HttpClient.Version version() {
        return java.net.http.HttpClient.Version.HTTP_1_1;
    }

    @Override
    public java.util.Optional<java.util.concurrent.Executor> executor() {
        return java.util.Optional.empty();
    }

    @Override
    public <T> HttpResponse<T> send(HttpRequest request, HttpResponse.BodyHandler<T> responseBodyHandler) throws java.io.IOException, InterruptedException {
        lastRequest = request;
        @SuppressWarnings("unchecked")
        HttpResponse<T> r = (HttpResponse<T>) cannedResponse;
        return r;
    }

    @Override
    public <T> CompletableFuture<HttpResponse<T>> sendAsync(HttpRequest request, HttpResponse.BodyHandler<T> responseBodyHandler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> CompletableFuture<HttpResponse<T>> sendAsync(HttpRequest request, HttpResponse.BodyHandler<T> responseBodyHandler, HttpResponse.PushPromiseHandler<T> pushPromiseHandler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public java.util.Optional<java.net.Authenticator> authenticator() {
        return java.util.Optional.empty();
    }

    @Override
    public javax.net.ssl.SSLContext sslContext() {
        try { return javax.net.ssl.SSLContext.getDefault(); } catch (Exception e) { throw new RuntimeException(e); }
    }

    @Override
    public javax.net.ssl.SSLParameters sslParameters() {
        return new javax.net.ssl.SSLParameters();
    }

    @Override
    public java.util.Optional<java.net.ProxySelector> proxy() {
        return java.util.Optional.empty();
    }

    @Override
    public java.net.http.HttpClient.Redirect followRedirects() {
        return java.net.http.HttpClient.Redirect.NEVER;
    }

    @Override
    public java.util.Optional<java.time.Duration> connectTimeout() {
        return java.util.Optional.empty();
    }

    @Override
    public java.util.Optional<java.net.CookieHandler> cookieHandler() {
        return java.util.Optional.empty();
    }

    @Override
    public java.net.http.WebSocket.Builder newWebSocketBuilder() {
        throw new UnsupportedOperationException();
    }
}
