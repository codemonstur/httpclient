package httpclient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.http.HttpRequest;
import java.util.HashMap;
import java.util.Random;

import static java.nio.charset.StandardCharsets.US_ASCII;

// Mostly works. Couple of unclear weird stuff:
// - What am I supposed to do with the name of a key? What if it contains double
//   quotes? Is there an escaping mechanism? What if it contains a new line?
//   This thing is totally vulnerable to header injection this way.
// - I've guessed that the encoding for the text is US-ASCII. But I can't find
//   any official documentation saying that it is.
// - I guessed and found some references here and there that the new lines are
//   supposed to be CRLF. Again, where in the official docs does it say this?
// - Am I supposed to look through the content of a field if it happens to contain
//   the boundary information? The Content-Length header is optional, so it could
//   clash in principle.
// - Should there be a CRLF after the value of a field but before the boundary?
//   I've built one in right now, don't know if that is correct.
// - When you have a content-length header and there are some left over bytes
//   still there after you have read the value but before a boundary shows up,
//   what happens to them?
public final class MultipartForm implements RequestBody {

    private final HashMap<String, byte[]> data = new HashMap<>();
    private String boundary = null;

    public MultipartForm add(final String key, final byte[] value) {
        data.put(key, value);
        return this;
    }

    @Override
    public String getHeader() {
        if (boundary == null) boundary = "----" + randomHexData(20);
        return "multipart/form-data; boundary=" + boundary;
    }

    private static String randomHexData(final int length) {
        final var array = new byte[length];
        new Random().nextBytes(array);
        return encodeHex(array);
    }
    private static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();
    private static String encodeHex(final byte[] bytes) {
        final var output = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            final int v = bytes[i] & 0xFF;
            output[i * 2    ] = HEX_CHARS[v >>> 4];
            output[i * 2 + 1] = HEX_CHARS[v & 0x0F];
        }
        return new String(output);
    }

    private static final byte[] CRLF = "\r\n".getBytes(US_ASCII);
    @Override
    public HttpRequest.BodyPublisher build() {
        final var out = new ByteArrayOutputStream();
        final var boundaryBytes = ("--" + boundary).getBytes(US_ASCII);
        try {
            out.write(boundaryBytes);
            for (final var entry : data.entrySet()) {
                out.write(CRLF);
                out.write(("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"").getBytes(US_ASCII));
                out.write(CRLF);
                final byte[] value = entry.getValue();
                out.write(("Content-Length: " + value.length).getBytes(US_ASCII));
                out.write(CRLF);
                out.write(CRLF);
                out.write(value);
                out.write(CRLF);
                out.write(boundaryBytes);
            }
            out.write("--".getBytes(US_ASCII));
            return HttpRequest.BodyPublishers.ofByteArray(out.toByteArray());
        } catch (final IOException e) {
            // Impossible
            throw new RuntimeException(e);
        }
    }

}
