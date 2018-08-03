package com.varian.oiscn.base.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * Created by gbt1220 on 12/5/2016.
 */
@Slf4j
public class JsonSerializer<T> {
    private ObjectMapper objectMapper;

    public JsonSerializer() {
        objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS, false);
        objectMapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
        objectMapper.configure(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS, false);
    }

    public <T> T getObject(String json, Class<T> name) {
        try {
            return objectMapper.readValue(json, name);
        } catch (IOException e) {
            log.error("IOException: {}", e.getMessage());
        }
        return null;
    }

    public String getJson(T t) {
        try {
            return objectMapper.writeValueAsString(t);
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException: {}", e.getMessage());
        }
        return "";
    }
}
