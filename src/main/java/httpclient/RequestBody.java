package httpclient;

import java.net.http.HttpRequest;

public interface RequestBody {

    String getHeader();
    HttpRequest.BodyPublisher build();

}
