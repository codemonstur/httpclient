package httpclient;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpRequest;
import java.time.Duration;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class HttpCallRequestTest {

    @Test
    void uriBuildsCorrectUri() throws Exception {
        var resp = new FakeHttpResponse(200, new byte[0], Map.of("Content-Type", java.util.List.of("text/plain")), URI.create("http://example.com/"));
        var client = new FakeHttpClient(resp);
        var req = new HttpCallRequest(client, HeaderStrategy.SILENT_REMOVE_NULL_HEADERS)
                .uri("https://example.com:8080/foo/bar?x=1&y=2")
                .get();
        req.send(java.net.http.HttpResponse.BodyHandlers.ofByteArray());
        var r = client.lastRequest;
        assertEquals(URI.create("https://example.com:8080/foo/bar?x=1&y=2"), r.uri());
        assertEquals("GET", r.method());
    }

    @Test
    void silentStrategyRemovesNullHeaders() {
        var resp = new FakeHttpResponse(200, new byte[0], Map.of(), URI.create("http://example.com/"));
        var client = new FakeHttpClient(resp);
        var req = new HttpCallRequest(client, HeaderStrategy.SILENT_REMOVE_NULL_HEADERS)
                .hostname("example.com").get("/p").header("X", null).header("", "v");
        // no exception
        assertNotNull(req);
    }

    @Test
    void throwingStrategyThrowsOnNullHeader() {
        var client = new FakeHttpClient(new FakeHttpResponse(200, new byte[0], Map.of(), URI.create("http://example.com/")));
        var req = new HttpCallRequest(client, HeaderStrategy.THROW_EXCEPTION_ON_NULL).hostname("example.com");
        assertThrows(IllegalArgumentException.class, () -> req.header("X", null));
        assertThrows(IllegalArgumentException.class, () -> req.header("", "v"));
    }

    @Test
    void headerCaseInsensitiveReplace() throws Exception {
        var resp = new FakeHttpResponse(200, new byte[0], Map.of(), URI.create("http://example.com/"));
        var client = new FakeHttpClient(resp);
        new HttpCallRequest(client, HeaderStrategy.THROW_EXCEPTION_ON_NULL)
                .hostname("example.com").get("/p")
                .header("Content-Type", "a").header("content-type", "b")
                .send(java.net.http.HttpResponse.BodyHandlers.ofByteArray());
        var r = client.lastRequest;
        assertEquals(1, r.headers().allValues("Content-Type").size());
        assertEquals("b", r.headers().firstValue("Content-Type").orElse(null));
    }

    @Test
    void timeoutSet() throws Exception {
        var resp = new FakeHttpResponse(200, new byte[0], Map.of(), URI.create("http://example.com/"));
        var client = new FakeHttpClient(resp);
        new HttpCallRequest(client, HeaderStrategy.THROW_EXCEPTION_ON_NULL)
                .hostname("example.com").get("/p")
                .timeout(Duration.ofSeconds(5))
                .send(java.net.http.HttpResponse.BodyHandlers.ofByteArray());
        assertNotNull(client.lastRequest);
    }

    @Test
    void executeReturnsResponse() throws Exception {
        var resp = new FakeHttpResponse(204, "ok".getBytes(), Map.of("Content-Type", java.util.List.of("text/plain")), URI.create("http://example.com/"));
        var client = new FakeHttpClient(resp);
        var r = new HttpCallRequest(client, HeaderStrategy.THROW_EXCEPTION_ON_NULL)
                .hostname("example.com").get("/p").execute();
        assertEquals(204, r.response().statusCode());
        assertEquals("ok", new String(r.fetchBody()));
    }
}
