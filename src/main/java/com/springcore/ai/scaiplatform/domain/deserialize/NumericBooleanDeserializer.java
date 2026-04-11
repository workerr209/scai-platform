package com.springcore.ai.scaiplatform.domain.deserialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

/**
 * Deserializes a string "true"/"false" value into a {@code Boolean}.
 *
 * Useful when a client sends boolean fields as string literals
 * instead of JSON boolean tokens.
 */
public class NumericBooleanDeserializer extends JsonDeserializer<Boolean> {

    @Override
    public Boolean deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        return "true".equals(parser.getText());
    }
}