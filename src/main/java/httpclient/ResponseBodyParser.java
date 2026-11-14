package httpclient;

import java.io.IOException;

/**
 * The {@code ResponseBodyParser} interface.
 *
 * @param <U> the type parameter
 */
public interface ResponseBodyParser<U> {

    /**
     * Returns the result of {@code parse}.
     *
     * @param body the body
     * @return the result
     * @throws IOException if the operation fails
     */
    U parse(byte[] body) throws IOException;

}
