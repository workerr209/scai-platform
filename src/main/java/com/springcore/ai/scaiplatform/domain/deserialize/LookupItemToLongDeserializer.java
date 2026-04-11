package com.springcore.ai.scaiplatform.domain.deserialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

/**
 * Deserializes a JSON value into a {@code Long} ID.
 *
 * Accepts two input shapes:
 *   - Object with an "id" field: {@code {"id": 42, "label": "..."}} → 42L
 *   - Raw number:                {@code 42}                         → 42L
 */
public class LookupItemToLongDeserializer extends JsonDeserializer<Long> {

    @Override
    public Long deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);

        if (node.isObject() && node.has("id")) {
            return node.get("id").asLong();
        }

        if (node.isNumber()) {
            return node.asLong();
        }

        return null;
    }
}