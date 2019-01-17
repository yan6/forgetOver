package com.example.demo.service.cache;

import com.example.demo.service.utils.JSONUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author ywj
 */

@Component
public class RedisServiceImpl implements RedisService {

    private static final Logger LOG = LoggerFactory.getLogger(RedisServiceImpl.class);

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 默认过期时长，单位：秒
     */
    public static final long DEFAULT_EXPIRE = 60 * 60 * 24;

    /**
     * 不设置过期时长
     */
    public static final long NOT_EXPIRE = -1;

    @Override
    public boolean put(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, serialize(value));
        } catch (Exception e) {
            LOG.warn("", e);
            return false;
        }
        return true;
    }

    @Override
    public boolean put(String key, Object value, long timeout, TimeUnit unit) {
        if (timeout < 0) {
            return put(key, value);
        } else {
            return putWithTimeout(key, value, timeout, unit);
        }
    }

    @Override
    public <T> T get(String key, Class<T> type) {
        return deserialize(redisTemplate.opsForValue().get(key), type);
    }

    @Override
    public <T> T get(String key, Class<T> type, CacheCallable<T> callable) {
        return get(key, type, callable, null);
    }

    @Override
    public <T> T get(String key, Class<T> type, CacheCallable<T> callable, T defaultValue) {
        return get(key, type, callable, defaultValue, -1, null);
    }

    @Override
    public <T> T get(String key, Class<T> type, CacheCallable<T> callable, T defaultValue, long timeout, TimeUnit unit) {
        T actualValue = get(key, type);
        if (actualValue != null) {
        }
        if (actualValue == null && callable != null) {
            try {
                actualValue = callable.call(key);
                if (actualValue != null && put(key, actualValue, timeout, unit)) {
                }
            } catch (Exception e) {
                LOG.error("[ get key from callable failed ] Get " + key + " from " + " failed", e);
            }
        }
        if (actualValue == null) {
            actualValue = defaultValue;
        }
        return actualValue;
    }

    //TODO 对hash,list,set,zset等方法，参考写

    public boolean existsKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 重名名key，如果newKey已经存在，则newKey的原值被覆盖
     *
     * @param oldKey
     * @param newKey
     */
    public void renameKey(String oldKey, String newKey) {
        redisTemplate.rename(oldKey, newKey);
    }

    /**
     * newKey不存在时才重命名
     *
     * @param oldKey
     * @param newKey
     * @return 修改成功返回true
     */
    public boolean renameKeyNotExist(String oldKey, String newKey) {
        return redisTemplate.renameIfAbsent(oldKey, newKey);
    }

    /**
     * 删除key
     *
     * @param key
     */
    public void deleteKey(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 删除多个key
     *
     * @param keys
     */
    public void deleteKey(String... keys) {
        Set<String> kSet = Stream.of(keys).map(k -> k).collect(Collectors.toSet());
        redisTemplate.delete(kSet);
    }

    /**
     * 删除Key的集合
     *
     * @param keys
     */
    public void deleteKey(Collection<String> keys) {
        Set<String> kSet = keys.stream().map(k -> k).collect(Collectors.toSet());
        redisTemplate.delete(kSet);
    }

    /**
     * 设置key的生命周期
     *
     * @param key
     * @param time
     * @param timeUnit
     */
    public void expireKey(String key, long time, TimeUnit timeUnit) {
        redisTemplate.expire(key, time, timeUnit);
    }

    /**
     * 指定key在指定的日期过期
     *
     * @param key
     * @param date
     */
    public void expireKeyAt(String key, Date date) {
        redisTemplate.expireAt(key, date);
    }

    /**
     * 查询key的生命周期
     *
     * @param key
     * @param timeUnit
     * @return
     */
    public long getKeyExpire(String key, TimeUnit timeUnit) {
        return redisTemplate.getExpire(key, timeUnit);
    }

    /**
     * 将key设置为永久有效
     *
     * @param key
     */
    public void persistKey(String key) {
        redisTemplate.persist(key);
    }

    private boolean putWithTimeout(String key, Object value, long timeout, TimeUnit unit) {
        try {
            redisTemplate.opsForValue().set(key, serialize(value), timeout, unit);
        } catch (Exception e) {
            LOG.warn("", e);
            return false;
        }
        return true;
    }

    private <T> T deserialize(String serializedValue, Class<T> targetClass) {
        if (serializedValue == null) {
            return null;
        }
        if (targetClass == String.class) {
            return (T) serializedValue;
        }
        return JSONUtils.fromJSON(serializedValue, targetClass);
    }

    private String serialize(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof String) {
            return (String) value;
        }
        return JSONUtils.toJSON(value);
    }
}
