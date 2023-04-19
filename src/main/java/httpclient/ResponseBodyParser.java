package httpclient;

import java.io.IOException;

public interface ResponseBodyParser<U> {

    U parse(byte[] body) throws IOException;

}
