package com.example.demo.service.cache;

import com.example.demo.service.utils.JSONUtils;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;

public class JacksonValueSerializer implements ValueSerializer {

    @Override
    public String serialize(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof String) {
            return (String) value;
        }
        return JSONUtils.toJSON(value);
    }

    @Override
    public List<String> serialize(List values) {
        if (CollectionUtils.isEmpty(values)) {
            return Collections.emptyList();
        }

        List<String> strValues = new ArrayList<>();
        for (Object value : values) {
            String strValue = serialize(value);
            strValues.add(strValue);
        }

        return strValues;
    }

    @Override
    public <T> T deserialize(String serializedValue, Class<T> targetClass) {
        if (serializedValue == null) {
            return null;
        }
        if (targetClass == String.class) {
            return (T) serializedValue;
        }
        return JSONUtils.fromJSON(serializedValue, targetClass);
    }

    @Override
    public <T> List<T> deserialize(List<String> serializedValues, Class<T> targetClass) {
        if (serializedValues == null) {
            return new LinkedList<>();
        }
        if (targetClass == String.class) {
            return (List<T>) serializedValues;
        }

        List<T> values = new ArrayList<>();
        for (String serializedValue : serializedValues) {
            T value = JSONUtils.fromJSON(serializedValue, targetClass);
            values.add(value);
        }
        return values;
    }

    @Override
    public <T> Set<T> deserialize(Set<String> serializedValues, Class<T> targetClass) {
        if (serializedValues == null) {
            return Collections.EMPTY_SET;
        }
        if (targetClass == String.class) {
            return (Set<T>) serializedValues;
        }

        Set<T> values = new HashSet<>();
        for (String serializedValue : serializedValues) {
            T value = JSONUtils.fromJSON(serializedValue, targetClass);
            values.add(value);
        }
        return values;
    }
}
