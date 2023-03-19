package httpclient;

import httpclient.Serializers.Serializer;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.util.Optional;
import java.util.function.Function;

public class HttpCallResponse {

    public interface Rule {
        void verify(HttpResponse<?> response) throws IOException;
    }

    private final Serializers serializers;
    private final HttpResponse<byte[]> response;

    public HttpCallResponse(final Serializers serializers, final HttpResponse<byte[]> response) {
        this.serializers = serializers;
        this.response = response;
    }

    public HttpCallResponse verifyNot404() throws IOException {
        if (response.statusCode() == 404) throw new IOException("HTTP call returned 404 Not Found");
        return this;
    }
    public HttpCallResponse verifyNotServerError() throws IOException {
        if (response.statusCode() >= 500 && response.statusCode() <= 599)
            throw new IOException("Remote server error. Status code is " + response.statusCode() + " for " + response.uri());
        return this;
    }

    public HttpCallResponse verifyStatusCode(final int code) throws IOException {
        if (response.statusCode() != code)
            throw new IOException("Wrong status code. Expected " + code + ", but was " + response.statusCode());
        return this;
    }
    public HttpCallResponse verifySuccess() throws IOException {
        if (response.statusCode() < 200 || response.statusCode() > 299)
            throw new IOException("Wrong status code. Expected 2XX, but was " + response.statusCode());
        return this;
    }
    public HttpCallResponse verifySuccess(final Function<HttpResponse<?>, ? extends IOException> supplier) throws IOException {
        if (response.statusCode() < 200 || response.statusCode() > 299)
            throw supplier.apply(response);
        return this;
    }

    public HttpCallResponse verify(final Rule rule) throws IOException {
        rule.verify(response);
        return this;
    }

    public <U> U fetchBodyInto(final Class<U> clazz) throws IOException {
        final var type = response.headers().firstValue("Content-Type");
        if (type.isEmpty()) throw new IOException("Content-Type missing from response");
        final var serializer = serializers.getFromHeader(type.get());
        if (serializer == null) throw new IOException("No serializer available for type: " + type.get());
        return serializer.fromData(response.body(), clazz);
    }
    public <U> U fetchBodyInto(final Serializer serializer, final Class<U> clazz) throws IOException {
        return serializer.fromData(response.body(), clazz);
    }
    public <U> U fetchBodyWith(final Function<byte[], U> converter) throws IOException {
        return converter.apply(response.body());
    }
    public byte[] fetchBody() throws IOException {
        return response.body();
    }
    public String fetchBodyAsString(final Charset charset) throws IOException {
        return new String(response.body(), charset);
    }

    public HttpResponse<byte[]> response() {
        return response;
    }

}
