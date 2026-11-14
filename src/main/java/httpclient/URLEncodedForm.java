package httpclient;

import java.net.URLEncoder;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * The {@code URLEncodedForm} class.
 */
public final class URLEncodedForm implements RequestBody {

    /**
     * Returns the result of {@code newFormBody}.
     *
     * @return the result
     */
    public static URLEncodedForm newFormBody() {
        return new URLEncodedForm();
    }

    /**
     * Returns the header.
     *
     * @return the result
     */
    public String getHeader() {
        return "application/x-www-form-urlencoded";
    }

    private final StringBuilder builder = new StringBuilder();

    /**
     * Returns the result of {@code add}.
     *
     * @param name the name
     * @param value the value
     * @return the result
     */
    public URLEncodedForm add(final String name, final String value) {
        if (builder.length() > 0) builder.append("&");
        builder.append(encodeUrl(name)).append("=").append(encodeUrl(value));
        return this;
    }
    /**
     * Returns the result of {@code build}.
     *
     * @return the result
     */
    public BodyPublisher build() {
        return BodyPublishers.ofString(builder.toString());
    }

    private static String encodeUrl(final String value) {
        return value != null ? URLEncoder.encode(value, UTF_8) : "";
    }


    /**
     * Creates a new instance.
     */
    public URLEncodedForm() {
    }
}
