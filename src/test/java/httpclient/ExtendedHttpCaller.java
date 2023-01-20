package httpclient;

import com.google.gson.Gson;
import httpclient.HttpCallRequest;
import httpclient.HttpCallResponse;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;

import static java.net.http.HttpResponse.BodyHandlers.ofString;

public final class ExtendedHttpCaller {

    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    public static final Gson GSON = new Gson();

    public static CustomHttpCallRequest newHttpCall() {
        return new CustomHttpCallRequest();
    }

    public static class CustomHttpCallRequest extends HttpCallRequest<CustomHttpCallRequest> {
        public CustomHttpCallRequest() {
            super(HTTP_CLIENT);
        }
        @Override public CustomHttpCallResponse execute() throws IOException {
            return new CustomHttpCallResponse(send(ofString()));
        }
    }
    public static class CustomHttpCallResponse extends HttpCallResponse<CustomHttpCallResponse, String> {
        public CustomHttpCallResponse(final HttpResponse<String> response) {
            super(response);
        }
        public <T> T fetchBodyInto(final Class<T> clazz) throws IOException {
            return GSON.fromJson(fetchBody(), clazz);
        }
    }
}
