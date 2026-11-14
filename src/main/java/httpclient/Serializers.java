package httpclient;

import java.util.HashMap;
import java.util.Map;

/**
 * The {@code Serializers} class.
 */
public final class Serializers {

    /**
     * The {@code Serializer} interface.
     */
    public interface Serializer {
        /**
         * Returns the result of {@code fromData}.
         *
         * @param <U> the type parameter
         * @param data the data
         * @param clazz the clazz
         * @return the result
         */
        <U> U fromData(byte[] data, Class<U> clazz);
    }

    /**
     * The {@code Format} enum.
     */
    public enum Format {
        /**
         * The {@code json} constant.
         */
        json, /**
     * The {@code xml} constant.
     */
    xml
    }

    private final Serializer json;
    private final Serializer xml;
    private final Map<String, Serializer> types;

    /**
     * Creates a new instance.
     *
     * @param json the json
     * @param xml the xml
     * @param types the types
     */
    public Serializers(final Serializer json, final Serializer xml, final Map<String, Serializer> types) {
        this.json = json;
        this.xml = xml;
        this.types = types;
    }

    /**
     * Returns the result of {@code getFromFormat}.
     *
     * @param format the format
     * @return the result
     */
    public Serializer getFromFormat(final Format format) {
        switch (format) {
            case json: return this.json;
            case xml: return this.xml;
            default: throw new IllegalArgumentException("Unknown format: " + format);
        }
    }

    /**
     * Returns the result of {@code getFromHeader}.
     *
     * @param contentType the content type
     * @return the result
     */
    public Serializer getFromHeader(final String contentType) {
        if (contentType == null) return null;
        final var serializer = types.get(contentType);
        if (serializer != null) return serializer;
        // responses usually append parameters ("application/json; charset=utf-8")
        // or vary casing: match on the bare media type
        final var mediaType = contentType.split(";")[0].trim().toLowerCase();
        return types.get(mediaType);
    }

    /**
     * The {@code Builder} class.
     */
    public static class Builder {

        private Serializer json;
        private Serializer xml;
        private Map<String, Serializer> types = new HashMap<>();

        /**
         * Returns the result of {@code json}.
         *
         * @param json the json
         * @return the result
         */
        public Builder json(final Serializer json) {
            this.json = json;
            this.types.put("application/json", json);
            this.types.put("application/json; charset=utf-8", json);
            return this;
        }
        /**
         * Returns the result of {@code xml}.
         *
         * @param xml the xml
         * @return the result
         */
        public Builder xml(final Serializer xml) {
            this.xml = xml;
            this.types.put("application/xml", xml);
            return this;
        }
        /**
         * Returns the result of {@code type}.
         *
         * @param type the type
         * @param serializer the serializer
         * @return the result
         */
        public Builder type(final String type, final Serializer serializer) {
            this.types.put(type, serializer);
            return this;
        }

        /**
         * Returns the result of {@code build}.
         *
         * @return the result
         */
        public Serializers build() {
            return new Serializers(json, xml, types);
        }

        /**
         * Creates a new instance.
         */
        public Builder() {
        }
    }

}
