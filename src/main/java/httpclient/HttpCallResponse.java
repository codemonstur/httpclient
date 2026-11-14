package httpclient;

import httpclient.Serializers.Serializer;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.util.Optional;
import java.util.function.Function;

/**
 * The {@code HttpCallResponse} class.
 */
public class HttpCallResponse {

    /**
     * The {@code Rule} interface.
     */
    public interface Rule {
        /**
         * Performs the {@code verify} operation.
         *
         * @param response the response
         * @throws IOException if the operation fails
         */
        void verify(HttpResponse<?> response) throws IOException;
    }

    private final Serializers serializers;
    private final HttpResponse<byte[]> response;

    /**
     * Creates a new instance.
     *
     * @param serializers the serializers
     * @param response the response
     */
    public HttpCallResponse(final Serializers serializers, final HttpResponse<byte[]> response) {
        this.serializers = serializers;
        this.response = response;
    }

    /**
     * Returns the result of {@code verifyNot404}.
     *
     * @return the result
     * @throws IOException if the operation fails
     */
    public HttpCallResponse verifyNot404() throws IOException {
        if (response.statusCode() == 404) throw new IOException("HTTP call returned 404 Not Found");
        return this;
    }
    /**
     * Returns the result of {@code verifyNotServerError}.
     *
     * @return the result
     * @throws IOException if the operation fails
     */
    public HttpCallResponse verifyNotServerError() throws IOException {
        if (response.statusCode() >= 500 && response.statusCode() <= 599)
            throw new IOException("Remote server error. Status code is " + response.statusCode() + " for " + response.uri());
        return this;
    }

    /**
     * Returns the result of {@code verifyStatusCode}.
     *
     * @param code the code
     * @return the result
     * @throws IOException if the operation fails
     */
    public HttpCallResponse verifyStatusCode(final int code) throws IOException {
        if (response.statusCode() != code)
            throw new IOException("Wrong status code. Expected " + code + ", but was " + response.statusCode());
        return this;
    }
    /**
     * Returns the result of {@code verifySuccess}.
     *
     * @return the result
     * @throws IOException if the operation fails
     */
    public HttpCallResponse verifySuccess() throws IOException {
        if (response.statusCode() < 200 || response.statusCode() > 299)
            throw new IOException("Wrong status code. Expected 2XX, but was " + response.statusCode());
        return this;
    }
    /**
     * Returns the result of {@code verifySuccess}.
     *
     * @param supplier the supplier
     * @return the result
     * @throws IOException if the operation fails
     */
    public HttpCallResponse verifySuccess(final Function<HttpResponse<?>, ? extends IOException> supplier) throws IOException {
        if (response.statusCode() < 200 || response.statusCode() > 299) {
            final var exception = supplier.apply(response);
            // a null from the supplier would make "throw null" surface as a bare NPE
            throw exception != null
                ? exception
                : new IOException("Wrong status code. Expected 2XX, but was " + response.statusCode());
        }
        return this;
    }

    /**
     * Returns the result of {@code verify}.
     *
     * @param rule the rule
     * @return the result
     * @throws IOException if the operation fails
     */
    public HttpCallResponse verify(final Rule rule) throws IOException {
        rule.verify(response);
        return this;
    }

    /**
     * Returns the result of {@code fetchBodyInto}.
     *
     * @param <U> the type parameter
     * @param clazz the clazz
     * @return the result
     * @throws IOException if the operation fails
     */
    public <U> U fetchBodyInto(final Class<U> clazz) throws IOException {
        final var type = response.headers().firstValue("Content-Type");
        if (type.isEmpty()) throw new IOException("Content-Type missing from response");
        final var serializer = serializers.getFromHeader(type.get());
        if (serializer == null) throw new IOException("No serializer available for type: " + type.get());
        return serializer.fromData(response.body(), clazz);
    }
    /**
     * Returns the result of {@code fetchBodyInto}.
     *
     * @param <U> the type parameter
     * @param serializer the serializer
     * @param clazz the clazz
     * @return the result
     * @throws IOException if the operation fails
     */
    public <U> U fetchBodyInto(final Serializer serializer, final Class<U> clazz) throws IOException {
        return serializer.fromData(response.body(), clazz);
    }
    /**
     * Returns the result of {@code fetchBodyWith}.
     *
     * @param <U> the type parameter
     * @param converter the converter
     * @return the result
     * @throws IOException if the operation fails
     */
    public <U> U fetchBodyWith(final ResponseBodyParser<U> converter) throws IOException {
        return converter.parse(response.body());
    }
    /**
     * Returns the result of {@code fetchBody}.
     *
     * @return the result
     * @throws IOException if the operation fails
     */
    public byte[] fetchBody() throws IOException {
        return response.body();
    }
    /**
     * Returns the result of {@code fetchBodyAsString}.
     *
     * @param charset the charset
     * @return the result
     * @throws IOException if the operation fails
     */
    public String fetchBodyAsString(final Charset charset) throws IOException {
        return new String(response.body(), charset);
    }

    /**
     * Returns the result of {@code response}.
     *
     * @return the result
     */
    public HttpResponse<byte[]> response() {
        return response;
    }

}
