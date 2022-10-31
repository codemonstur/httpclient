package httpclient;

import java.net.URLEncoder;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;

import static java.nio.charset.StandardCharsets.UTF_8;

public final class URLEncodedForm {

    public static URLEncodedForm newFormBody() {
        return new URLEncodedForm();
    }

    public String getHeader() {
        return "application/x-www-form-urlencoded";
    }

    private final StringBuilder builder = new StringBuilder();

    public URLEncodedForm add(final String name, final String value) {
        if (builder.length() > 0) builder.append("&");
        builder.append(encodeUrl(name)).append("=").append(encodeUrl(value));
        return this;
    }
    public BodyPublisher build() {
        return BodyPublishers.ofString(builder.toString());
    }

    public static String encodeUrl(final String value) {
        return value != null ? URLEncoder.encode(value, UTF_8) : "";
    }

}
