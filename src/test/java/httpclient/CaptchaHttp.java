package httpclient;

import java.io.IOException;

import static httpclient.ExtendedHttpCaller.newHttpCall;
import static httpclient.URLEncodedForm.newFormBody;

public enum CaptchaHttp {;

    // challenge_ts is the timestamp of the challenge load (ISO format yyyy-MM-dd'T'HH:mm:ssZZ)
    // There is an array of string called 'app.error-codes'. It's optional, I left it out because the dash
    // gets in the way
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

}
