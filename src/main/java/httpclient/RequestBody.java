package httpclient;

import java.net.http.HttpRequest;

/**
 * The {@code RequestBody} interface.
 */
public interface RequestBody {

    /**
     * Returns the header.
     *
     * @return the result
     */
    String getHeader();
    /**
     * Returns the result of {@code build}.
     *
     * @return the result
     */
    HttpRequest.BodyPublisher build();

}
