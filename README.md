
[![GitHub Release](https://img.shields.io/github/release/codemonstur/simplexml.svg)](https://github.com/codemonstur/httpclient/releases)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.codemonstur/httpclient/badge.svg)](http://mvnrepository.com/artifact/com.github.codemonstur/httpclient)
[![MIT Licence](https://badges.frapsoft.com/os/mit/mit.svg?v=103)](https://opensource.org/licenses/mit-license.php)

# HttpClient

A wrapper around the Java 11 HttpClient that makes for nicer code.

With some tweaking you can get this:

    static class RecaptchaResponse { boolean success; String challenge_ts; String hostname; }

    public static void main(final String... args) throws IOException {
        final var response = newHttpCall()
            .scheme("https").hostname("www.google.com").port(443)
            .post("/recaptcha/api/siteverify")
            .body(newFormBody()
                .add("secret", "1234567890")
                .add("response", "response"))
            .execute()
            .verifyNotServerError()
            .verifySuccess()
            .fetchBodyInto(RecaptchaResponse.class);
    }

It's in Maven Central

    <dependency>
        <groupId>com.github.codemonstur</groupId>
        <artifactId>httpclient</artifactId>
        <version>1.0.0</version>
    </dependency>
