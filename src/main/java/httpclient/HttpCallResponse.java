package httpclient;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.function.Function;

public class HttpCallResponse<T extends HttpCallResponse<T, B>, B> {

    public interface Serializer<C> {
        <U> U fromData(C data, Class<U> clazz);
    }
    public interface Rule {
        void verify(HttpResponse<?> response) throws IOException;
    }

    private final HttpResponse<B> response;

    public HttpCallResponse(final HttpResponse<B> response) {
        this.response = response;
    }

    public T verifyNot404() throws IOException {
        if (response.statusCode() == 404) throw new IOException("HTTP call returned 404 Not Found");
        return (T) this;
    }
    public T verifyNotServerError() throws IOException {
        if (response.statusCode() >= 500 && response.statusCode() <= 599)
            throw new IOException("Remote server error. Status code is " + response.statusCode() + " for " + response.uri());
        return (T) this;
    }

    public T verifyStatusCode(final int code) throws IOException {
        if (response.statusCode() != code)
            throw new IOException("Wrong status code. Expected " + code + ", but was " + response.statusCode());
        return (T) this;
    }
    public T verifySuccess() throws IOException {
        if (response.statusCode() < 200 || response.statusCode() > 299)
            throw new IOException("Wrong status code. Expected 2XX, but was " + response.statusCode());
        return (T) this;
    }
    public T verifySuccess(final Function<HttpResponse<?>, ? extends IOException> supplier) throws IOException {
        if (response.statusCode() < 200 || response.statusCode() > 299)
            throw supplier.apply(response);
        return (T) this;
    }

    public T verify(final Rule rule) throws IOException {
        rule.verify(response);
        return (T) this;
    }

    public <U> U fetchBodyInto(final Serializer<B> serializer, final Class<U> clazz) throws IOException {
        return serializer.fromData(response.body(), clazz);
    }
    public <U> U fetchBodyWith(final Function<B, U> converter) throws IOException {
        return converter.apply(response.body());
    }
    public B fetchBody() throws IOException {
        return response.body();
    }

}
