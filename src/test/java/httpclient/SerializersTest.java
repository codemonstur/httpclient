package httpclient;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SerializersTest {

    @Test
    void getFromHeaderExactAndParameterized() {
        var s = new Serializers.Serializer() {
            public <U> U fromData(byte[] data, Class<U> clazz) { return null; }
        };
        var serializers = new Serializers.Builder().json(s).xml(s).type("text/plain", s).build();
        assertSame(s, serializers.getFromHeader("application/json"));
        assertSame(s, serializers.getFromHeader("application/json; charset=utf-8"));
        assertSame(s, serializers.getFromHeader("Application/JSON"));
        assertSame(s, serializers.getFromHeader("text/plain"));
        assertNull(serializers.getFromHeader(null));
        assertNull(serializers.getFromHeader("unknown/type"));
    }

    @Test
    void getFromFormat() {
        var s = new Serializers.Serializer() {
            public <U> U fromData(byte[] data, Class<U> clazz) { return null; }
        };
        var serializers = new Serializers.Builder().json(s).xml(s).build();
        assertSame(s, serializers.getFromFormat(Serializers.Format.json));
        assertSame(s, serializers.getFromFormat(Serializers.Format.xml));
        assertThrows(NullPointerException.class, () -> serializers.getFromFormat(null));
    }
}
