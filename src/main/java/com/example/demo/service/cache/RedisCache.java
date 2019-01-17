package com.example.demo.service.cache;

import com.example.demo.service.exception.LockException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.*;
import org.springframework.util.Assert;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class RedisCache implements Cache {

    private static final Log LOG = LogFactory.getLog(RedisCache.class);
    private static final int DEFAULT_EXPIRE_TIME_IN_SECOND = 0;  // Never expired

    private final StringRedisTemplate redisTemplate;
    private String name;
    private ValueSerializer valueSerializer;

    public RedisCache(StringRedisTemplate redisTemplate) {
        this(null, redisTemplate);
    }

    public RedisCache(String name, StringRedisTemplate redisTemplate) {
        this.name = name;
        this.redisTemplate = redisTemplate;
        this.valueSerializer = new JacksonValueSerializer();
    }

    public void setValueSerializer(ValueSerializer valueSerializer) {
        this.valueSerializer = valueSerializer;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void init() {
        Assert.notNull(name, "RedisCache.name should not be null");
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public <T> T get(String key, Class<T> type) {
        return valueSerializer.deserialize(redisTemplate.opsForValue().get(key), type);
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
                LOG.error("[ get key from callable failed ] Get " + key + " from " + getName() + " failed", e);
            }
        }
        if (actualValue == null) {
            actualValue = defaultValue;
        }
        return actualValue;
    }

    @Override
    public <T> List<T> multiGet(List<String> keys, Class<T> type) {
        return valueSerializer.deserialize(redisTemplate.opsForValue().multiGet(keys), type);
    }

    @Override
    public <T> List<T> multiGet(List<String> keys, Class<T> type, CacheCallable<T> callable) {
        return multiGet(keys, type, callable, null, -1, null);
    }

    @Override
    public <T> List<T> multiGet(List<String> keys, Class<T> type, CacheCallable<T> callable, T defaultValue, long timeout, TimeUnit unit) {
        List<T> actualList = multiGet(keys, type);
        if (actualList.contains(null)) {
            actualList = keys
                    .stream()
                    .map(key -> get(key, type, callable, defaultValue, timeout, unit))
                    .collect(Collectors.toList());
        }
        return actualList;
    }

    @Override
    public boolean put(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, valueSerializer.serialize(value));
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
    public boolean evict(String key) {
        redisTemplate.delete(key);
        return true;
    }

    @Override
    public <T> List<T> getList(String key, int start, int num, Class<T> type) {
        int end = num >= 0 ? start + num - 1 : num;
        List<String> strValues = redisTemplate.opsForList().range(key, start, end);

        return valueSerializer.deserialize(strValues, type);
    }

    @Override
    public Long getListSize(String key) {
        return redisTemplate.opsForList().size(key);
    }

    @Override
    public boolean putList(String key, List values) {
        if (CollectionUtils.isEmpty(values)) {
            LOG.warn("values cannot be null or empty");
            return false;
        }

        evict(key);

        List<String> strValues = valueSerializer.serialize(values);

        try {
            redisTemplate.opsForList().rightPushAll(key, strValues);
        } catch (Exception e) {
            LOG.warn("", e);
            return false;
        }
        return true;
    }

    @Override
    public boolean putList(String key, Object value) {
        if (Objects.isNull(value)) {
            LOG.warn("value cannot be null");
            return false;
        }
        String strValue = valueSerializer.serialize(value);
        try {
            redisTemplate.opsForList().rightPush(key, strValue);
        } catch (Exception e) {
            LOG.warn("", e);
            return false;
        }
        return true;
    }

    @Override
    public boolean leftPushListIfPresent(String key, Object value) {
        if (Objects.isNull(value)) {
            LOG.warn("value cannot be null");
            return false;
        }

        String strValue = valueSerializer.serialize(value);
        try {
            redisTemplate.opsForList().leftPushIfPresent(key, strValue);
        } catch (Exception e) {
            LOG.warn("", e);
            return false;
        }
        return true;
    }

    @Override
    public boolean removeFromList(String key, Object value, int count) {
        if (Objects.isNull(value)) {
            LOG.warn("value cannot be null");
            return false;
        }

        String strValue = valueSerializer.serialize(value);
        try {
            redisTemplate.opsForList().remove(key, count, strValue);
        } catch (Exception e) {
            LOG.warn("", e);
            return false;
        }
        return true;
    }

    @Override
    public void updateList(String key, Object o, boolean isAdd) {
        if (!hasKey(key)) {
            return;
        }
        removeFromList(key, o, 1);
        if (isAdd) {
            leftPushListIfPresent(key, o);
        }
    }

    @Override
    public <T> Set<T> sMembers(String key, Class<T> type) {
        Set<String> strValues = redisTemplate.opsForSet().members(key);
        return valueSerializer.deserialize(strValues, type);
    }

    @Override
    public <T> List<T> sRandMember(String key, int count, Class<T> type) {
        List<String> strValues = redisTemplate.opsForSet().randomMembers(key, count);
        return valueSerializer.deserialize(strValues, type);
    }

    @Override
    public <T> boolean sadd(String key, Class<T> value) {
        if (value == null) {
            LOG.warn("values cannot be null or empty");
            return false;
        }
        String valueStr = valueSerializer.serialize(value);
        try {
            return redisTemplate.opsForSet().add(key, valueStr) > 0;
        } catch (Exception e) {
            LOG.warn("", e);
            return false;
        }
    }

    @Override
    public boolean addToZSet(String key, Set<ZSetOperations.TypedTuple<String>> typedTuples) {
        return addsToZSet(key, typedTuples);
    }

    @Override
    public boolean addToZSet(String key, Object value, double score) {
        try {
            redisTemplate.opsForZSet().add(key, valueSerializer.serialize(value), score);
        } catch (Exception e) {
            LOG.warn("", e);
            return false;
        }
        return true;
    }

    @Override
    public boolean addsToZSet(String key, Set<ZSetOperations.TypedTuple<String>> tupleSet) {
        try {
            redisTemplate.opsForZSet().add(key, tupleSet);
        } catch (Exception e) {
            LOG.warn("", e);
            return false;
        }
        return true;
    }

    @Override
    public Set<String> rangeFromZSet(String key, long start, long end) {
        return redisTemplate.opsForZSet().reverseRange(key, start, end);
    }

    @Override
    public Set<ZSetOperations.TypedTuple<String>> rangeFromZSetWithScore(String key, long start, long end) {
        return redisTemplate.opsForZSet().reverseRangeWithScores(key, start, end);
    }

    @Override
    public Set<String> rangeFromZSetByScore(String key, double startScore, double endScore) {
        return redisTemplate.opsForZSet().rangeByScore(key, startScore, endScore);
    }

    @Override
    public Set<ZSetOperations.TypedTuple<String>> revRangeFromZSetByScoreWithCount(String key, double startScore, double endScore, long offset, long count) {
        return redisTemplate.opsForZSet().reverseRangeByScoreWithScores(key, startScore, endScore, offset, count);
    }

    @Override
    public Set<ZSetOperations.TypedTuple<String>> revRangeFromZSetByScore(String key, double startScore, double endScore) {
        return redisTemplate.opsForZSet().reverseRangeByScoreWithScores(key, startScore, endScore);
    }

    @Override
    public Double zscore(String key, String item) {
        return redisTemplate.opsForZSet().score(key, item);
    }

    @Override
    public Long removeRangeByScore(String key, double startScore, double endScore) {
        return redisTemplate.opsForZSet().removeRangeByScore(key, startScore, endScore);
    }

    @Override
    public Long removeRangeFromZset(String key, long startIndex, long endIndex) {
        return redisTemplate.opsForZSet().removeRange(key, startIndex, endIndex);
    }

    @Override
    public long getZSetCount(String key) {
        return redisTemplate.opsForZSet().zCard(key);
    }

    @Override
    public long getZSetCount(String key, double startScore, double endScore) {
        return redisTemplate.opsForZSet().count(key, startScore, endScore);
    }

    @Override
    public Set<ZSetOperations.TypedTuple<String>> rangeFromZSetWithScores(String key, Long start) {
        return redisTemplate.opsForZSet().reverseRangeByScoreWithScores(key, 0, start);
    }

    @Override
    public Set<ZSetOperations.TypedTuple<String>> rangeFromZSetWithScores(String key, Long start, long count) {
        return redisTemplate.opsForZSet().reverseRangeByScoreWithScores(key, 0, start, 0, count);
    }

    @Override
    public <T> Set<T> rangeFromZSetByScore(String key, Integer score, Class<T> type, CacheListCallable callable, Set<T> defaultValue) {
        Set<T> actualValue = valueSerializer.deserialize(redisTemplate.opsForZSet().reverseRangeByScore(key, 0, score), type);
        if (actualValue != null) {
        }
        if ((actualValue == null || actualValue.isEmpty()) && callable != null) {
            try {
                Set<ZSetOperations.TypedTuple<String>> realValue = callable.listCall(key);
                if (realValue != null && !realValue.isEmpty() && addToZSet(key, realValue)) {
                    actualValue = valueSerializer.deserialize(redisTemplate.opsForZSet().reverseRangeByScore(key, 0, score), type);
                }
            } catch (Exception e) {
                logWarningMessage("Get " + key + " from " + getName() + " failed", e);
            }
        }
        if (actualValue == null || actualValue.isEmpty()) {
            actualValue = defaultValue;
        }
        return actualValue;
    }


    @Override
    public boolean removeFromZSet(String key, Object value) {
        redisTemplate.opsForZSet().remove(key, valueSerializer.serialize(value));
        return true;
    }

    @Override
    public boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    @Override
    public boolean expire(String key, long timeout, TimeUnit unit) {
        return redisTemplate.expire(key, timeout, unit);
    }

    @Override
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public void addToMap(String key, Map map) {
        evict(key);
        redisTemplate.opsForHash().putAll(key, map);
    }

    @Override
    public <K, V> Map<K, V> getMap(String key) {
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
        Map<K, V> result = new HashMap<>();
        entries.entrySet().forEach(entry -> {
            result.put((K) entry.getKey(), (V) entry.getValue());
        });
        return result;
    }

    @Override
    public boolean setBit(String key, int offset, boolean value) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        return valueOperations.setBit(key, offset, value);
    }

    @Override
    public byte[] getBit(String key) {
        return redisTemplate.execute(new RedisCallback<byte[]>() {
            @Override
            public byte[] doInRedis(RedisConnection connection) throws DataAccessException {
                byte[] bytes = connection.get(key.getBytes());
                return bytes;
            }
        });
//        RedisConnection connection = redisTemplate.getConnectionFactory().getConnection();
//        byte[] bytes = connection.get(key.getBytes());
//        connection.close();
//        return bytes;
    }

    @Override
    public <T> T lock(String key, long time, TimeUnit timeUnit, MutexProcessWithReturn<T> processWithReturn) throws LockException {
        //todo 检查key是否存在和set Key还是非原子操作，有几率一起锁定
        if (hasKey(key)) {
            throw new LockException(key);
        }
        try {
            put(key, Long.toString(time), time, timeUnit);
            return processWithReturn.process();
        } catch (Exception e) {
            LOG.error("[lock] key=" + key + ",error=" + e);
        } finally {
            delete(key);
        }
        throw new LockException(key);
    }

    @Override
    public Long incrByOne(String key) {
        return redisTemplate.opsForValue().increment(key, 1L);
    }

    @Override
    public Long decrByOne(String key) {
        return redisTemplate.opsForValue().increment(key, -1L);
    }

    @Override
    public Long safeDecrByOne(String key) {
        Long count = redisTemplate.opsForValue().increment(key, -1L);
        if (count < 0) {
            put(key, 0);
            return 0L;
        }
        return count;
    }

    @Override
    public Set<String> members(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    @Override
    public boolean addToSet(String key, Object value) {
        try {
            redisTemplate.opsForSet().add(key, valueSerializer.serialize(value));
        } catch (Exception e) {
            LOG.warn("", e);
            return false;
        }
        return true;
    }

    @Override
    public boolean addToSet(String key, List values) {
        List<String> strValues = valueSerializer.serialize(values);

        try {
            redisTemplate.opsForSet().add(key, strValues.toArray(new String[0]));
        } catch (Exception e) {
            LOG.warn("", e);
            return false;
        }
        return true;
    }

    @Override
    public boolean removeFromSet(String key, Object value) {
        redisTemplate.opsForSet().remove(key, valueSerializer.serialize(value));
        return true;
    }

    @Override
    public boolean contains(String key, Object value) {
        return redisTemplate.opsForSet().isMember(key, valueSerializer.serialize(value));
    }

    @Override
    public boolean evictByPattern(String keyPattern) {
        Set<String> keys = redisTemplate.keys(keyPattern);
        for (String key : keys) {
            redisTemplate.delete(key);
        }
        return true;
    }

    private void logWarningMessage(String message, Exception e) {
        LOG.warn(String.format("Cache: %s; Message: %s", name, message), e);
    }

    private boolean putWithTimeout(String key, Object value, long timeout, TimeUnit unit) {
        try {
            redisTemplate.opsForValue().set(key, valueSerializer.serialize(value), timeout, unit);
        } catch (Exception e) {
            LOG.warn("", e);
            return false;
        }
        return true;
    }

    @Override
    public boolean addToGeo(String key, double x, double y, String member) {
        try {
            LOG.info("addToGeo, key:" + key + ",member:" + member);
            GeoOperations<String, String> geoOps = redisTemplate.opsForGeo();
            geoOps.geoAdd(key, new Point(x, y), member);
        } catch (Exception e) {
            LOG.warn("error during addToCacheGeo", e);
            return false;
        }
        return true;
    }

    @Override
    public boolean removeFromGeo(String key, String... members) {
        try {
            GeoOperations<String, String> geoOps = redisTemplate.opsForGeo();
            geoOps.geoRemove(key, members);
        } catch (Throwable t) {
            LOG.warn("error during removeFromGeo", t);
            return false;
        }
        return true;
    }

    @Override
    public Distance distanceGeo(String key, String member1, String member2) {
        try {
            GeoOperations<String, String> geoOps = redisTemplate.opsForGeo();
            return geoOps.geoDist(key, member1, member2);
        } catch (Throwable t) {
            LOG.warn("error during distanceGeo", t);
        }
        return null;
    }

    @Override
    public GeoResults<RedisGeoCommands.GeoLocation<String>> radiusGeo(String key, double x, double y,
                                                                      double distance, RedisGeoCommands.DistanceUnit distanceUnit, Sort.Direction sortOrder, long count) {
        try {
            GeoOperations<String, String> geoOps = redisTemplate.opsForGeo();
            //设置geo查询参数,查询返回结果包括距离和坐标
            RedisGeoCommands.GeoRadiusCommandArgs geoRadiusArgs = RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs().includeCoordinates();
            if (Sort.Direction.ASC.equals(sortOrder)) {//按查询出的坐标距离中心坐标的距离进行排序
                geoRadiusArgs.sortAscending();
            } else if (Sort.Direction.DESC.equals(sortOrder)) {
                geoRadiusArgs.sortDescending();
            }
            geoRadiusArgs.limit(count);//限制查询数量
            GeoResults<RedisGeoCommands.GeoLocation<String>> radiusGeo = geoOps.geoRadius(key, new Circle(new Point(x, y), new Distance(distance, distanceUnit)), geoRadiusArgs);
            return radiusGeo;
        } catch (Throwable t) {
            LOG.warn("error during radiusGeo", t);
        }
        return null;
    }

}
