package httpclient;

import java.util.HashMap;
import java.util.Map;

public final class Serializers {

    public interface Serializer {
        <U> U fromData(byte[] data, Class<U> clazz);
    }

    public enum Format {
        json, xml
    }

    private final Serializer json;
    private final Serializer xml;
    private final Map<String, Serializer> types;

    public Serializers(final Serializer json, final Serializer xml, final Map<String, Serializer> types) {
        this.json = json;
        this.xml = xml;
        this.types = types;
    }

    public Serializer getFromFormat(final Format format) {
        return switch (format) {
            case json -> this.json;
            case xml -> this.xml;
        };
    }

    public Serializer getFromHeader(final String contentType) {
        return types.get(contentType);
    }

    public static class Builder {

        private Serializer json;
        private Serializer xml;
        private Map<String, Serializer> types = new HashMap<>();

        public Builder json(final Serializer json) {
            this.json = json;
            this.types.put("application/json", json);
            this.types.put("application/json; charset=utf-8", json);
            return this;
        }
        public Builder xml(final Serializer xml) {
            this.xml = xml;
            return this;
        }
        public Builder type(final String type, final Serializer serializer) {
            this.types.put(type, serializer);
            return this;
        }

        public Serializers build() {
            return new Serializers(json, xml, types);
        }
    }

}
