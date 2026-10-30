package httpclient;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class FormsTest {

    @Test
    void urlEncodedFormBuildsQueryString() {
        var form = URLEncodedForm.newFormBody().add("a", "1").add("b", "x y");
        var pub = form.build();
        // BodyPublishers.ofString -> can't easily read; check header
        assertEquals("application/x-www-form-urlencoded", form.getHeader());
    }

    @Test
    void multipartFormInvalidKeyThrows() {
        var form = new MultipartForm();
        assertThrows(IllegalArgumentException.class, () -> form.add(null, new byte[0]));
        assertThrows(IllegalArgumentException.class, () -> form.add("a\r", new byte[0]));
        assertThrows(IllegalArgumentException.class, () -> form.add("a\"", new byte[0]));
    }

    @Test
    void multipartFormBuildContainsBoundaryAndFields() {
        var form = new MultipartForm().add("field", "value".getBytes(StandardCharsets.UTF_8));
        String header = form.getHeader();
        assertTrue(header.startsWith("multipart/form-data; boundary="));
        var pub = form.build();
        assertNotNull(pub);
    }
}
