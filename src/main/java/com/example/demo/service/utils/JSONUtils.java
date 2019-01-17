package com.example.demo.service.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public final class JSONUtils {
    private static final Logger LOG = LoggerFactory.getLogger(JSONUtils.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final int JSON_LOG_THRESHOLD = 1024 * 2;  // 2 kB

    static {
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OBJECT_MAPPER.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
    }

    private JSONUtils() {
    }

    public static String toJSON(Object object) {
        try {
            if (object == null) {
                return StringUtils.EMPTY;
            }
            return OBJECT_MAPPER.writer().writeValueAsString(object);
        } catch (IOException e) {
            LOG.warn("Serialize " + object + " failed", e);
            return null;
        }
    }

    public static <T> T fromJSON(String json, Class<T> clazz) {
        try {
            if (StringUtils.isBlank(json)) {
                return null;
            }
            return OBJECT_MAPPER.readValue(json, clazz);
        } catch (IOException e) {
            if (StringUtils.isNotBlank(json) && json.length() <= JSON_LOG_THRESHOLD) {
                LOG.warn("Deserialize " + json + " failed", e);
            } else {
                LOG.warn("Deserialize failed", e);
            }
            return null;
        }
    }

    public static <T> T fromJSON(String json, TypeReference<T> typeReference) {
        try {
            if (StringUtils.isBlank(json)) {
                return null;
            }
            return OBJECT_MAPPER.readValue(json, typeReference);
        } catch (IOException e) {
            if (StringUtils.isNotBlank(json) && json.length() <= JSON_LOG_THRESHOLD) {
                LOG.warn("Deserialize " + json + " failed", e);
            } else {
                LOG.warn("Deserialize failed", e);
            }
            return null;
        }
    }

}
