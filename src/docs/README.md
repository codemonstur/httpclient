# httpclient

A small fluent HTTP client wrapper around the JDK's `java.net.http.HttpClient`.
**Zero runtime dependencies** (only test-scope JUnit and Gson).

## Usage

Build a request with a fluent DSL, execute it, then assert on the response and
deserialize the body:

```java
RecaptchaResponse body = newHttpCall()
    .scheme("https")
    .hostname("www.google.com")
    .port(443)
    .post("/recaptcha/api/siteverify")
    .body(newFormBody()
        .add("secret", secret)
        .add("response", token))
    .execute()
    .verifyNotServerError()
    .verifySuccess()
    .fetchBodyInto(RecaptchaResponse.class);
```

### Request building

`HttpCallRequest` supports scheme, hostname, port, path, query parameters, any
HTTP method, headers, and a body. Bodies are provided by `RequestBody`
implementations:

- `URLEncodedForm` (via `newFormBody()`) — standard `application/x-www-form-urlencoded`.
- `MultipartForm` — `multipart/form-data` uploads.

### Response verification

`HttpCallResponse` exposes composable verification rules that throw on failure:

- `verifyNot404()`
- `verifyNotServerError()`
- `verifySuccess()`
- Custom rules via the `Rule` interface.

`fetchBodyInto(Class)` deserializes the body using a registered `Serializer`
(JSON or XML, pluggable via the `Serializers` registry).

### Null header handling

`HeaderStrategy` controls behaviour when a header value is `null`:

- `SILENT_REMOVE_NULL_HEADERS` (drop them)
- `THROW_EXCEPTION_ON_NULL` (fail fast)

## Dependency

```xml
<dependency>
  <groupId>com.github.codemonstur</groupId>
  <artifactId>httpclient</artifactId>
  <version>1.2.2</version>
</dependency>
```

## Building

```sh
make build
make test
```

## License

MIT
