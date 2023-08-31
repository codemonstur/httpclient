package httpclient;

import com.google.gson.Gson;

import java.net.http.HttpClient;

import static java.net.http.HttpResponse.BodyHandlers.ofString;
import static java.nio.charset.StandardCharsets.UTF_8;
import static httpclient.HeaderStrategy.THROW_EXCEPTION_ON_NULL;

public enum Utils {;

    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    public static final Gson GSON = new Gson();

    public static HttpCallRequest newHttpCall() {
        return new HttpCallRequest(HTTP_CLIENT, new Serializers.Builder()
            .json(Utils::fromJson)
            .build(), THROW_EXCEPTION_ON_NULL);
    }

    private static <U> U fromJson(final byte[] data, final Class<U> clazz) {
        return GSON.fromJson(new String(data, UTF_8), clazz);
    }

}
