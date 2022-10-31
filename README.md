
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

