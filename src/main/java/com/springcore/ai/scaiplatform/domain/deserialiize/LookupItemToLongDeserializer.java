package com.springcore.ai.scaiplatform.domain.deserialiize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

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
