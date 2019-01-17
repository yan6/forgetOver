package com.example.demo.service.cache;

import com.example.demo.service.exception.LockException;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public interface Cache {

    /**
     * Return the cache name.
     */
    String getName();

    /**
     * Return the value to which this cache maps the specified key,
     * generically specifying a type that return value will be cast to.
     */
    <T> T get(String key, Class<T> type);

    /**
     * Return the value to which this cache maps the specified key,
     * if no value is found, calling callable to load the value
     */
    <T> T get(String key, Class<T> type, CacheCallable<T> callable);

    /**
     * Return the value to which this cache maps the specified key,
     * if no value is found, calling callable to load the value
     */
    <T> T get(String key, Class<T> type, CacheCallable<T> callable, T defaultValue);

    <T> T get(String key, Class<T> type, CacheCallable<T> callable, T defaultValue, long timeout, TimeUnit unit);

    <T> List<T> multiGet(List<String> keys, Class<T> type);

    <T> List<T> multiGet(List<String> keys, Class<T> type, CacheCallable<T> callable);

    <T> List<T> multiGet(List<String> keys, Class<T> type, CacheCallable<T> callable, T defaultValue, long timeout, TimeUnit unit);

    boolean put(String key, Object value);

    boolean put(String key, Object value, long timeout, TimeUnit unit);

    /**
     * Evict the mapping for this key from this cache if it is present.
     */
    boolean evict(String key);

    <T> List<T> getList(String key, int start, int num, Class<T> type);

    Long getListSize(String key);

    boolean putList(String key, List values);

    boolean putList(String key, Object value);

    boolean leftPushListIfPresent(String key, Object value);

    /**
     * count > 0 : 从表头开始向表尾搜索，移除与 VALUE 相等的元素，数量为 COUNT 。
     * count < 0 : 从表尾开始向表头搜索，移除与 VALUE 相等的元素，数量为 COUNT 的绝对值。
     * count = 0 : 移除表中所有与 VALUE 相等的值
     */
    boolean removeFromList(String key, Object value, int count);

    Long incrByOne(String key);

    Long decrByOne(String key);

    Long safeDecrByOne(String key);

    Set<String> members(String key);

    boolean addToSet(String key, Object value);

    boolean addToSet(String key, List values);

    boolean removeFromSet(String key, Object value);

    boolean contains(String key, Object value);

    boolean evictByPattern(String pattern);

    void updateList(String key, Object o, boolean isAdd);

    <T> Set<T> sMembers(String key, Class<T> type);

    <T> List<T> sRandMember(String key, int count, Class<T> type);

    <T> boolean sadd(String key, Class<T> value);

    boolean addToZSet(String key, Object value, double score);

    boolean addToZSet(String key, Set<ZSetOperations.TypedTuple<String>> tuples);

    boolean addsToZSet(String key, Set<ZSetOperations.TypedTuple<String>> tupleSet);

    Set<String> rangeFromZSet(String key, long start, long end);

    Set<ZSetOperations.TypedTuple<String>> rangeFromZSetWithScore(String key, long start, long end);

    Set<String> rangeFromZSetByScore(String key, double startScore, double endScore);

    Set<ZSetOperations.TypedTuple<String>> revRangeFromZSetByScoreWithCount(String key, double startScore, double endScore, long offset, long count);

    Set<ZSetOperations.TypedTuple<String>> revRangeFromZSetByScore(String key, double startScore, double endScore);

    Double zscore(String key, String item);

    Long removeRangeByScore(String key, double startScore, double endScore);

    Long removeRangeFromZset(String key, long startIndex, long endIndex);

    long getZSetCount(String key);

    long getZSetCount(String key, double startScore, double endScore);

    Set<ZSetOperations.TypedTuple<String>> rangeFromZSetWithScores(String key, Long start);

    Set<ZSetOperations.TypedTuple<String>> rangeFromZSetWithScores(String key, Long start, long count);

    <T> Set<T> rangeFromZSetByScore(String key, Integer score, Class<T> type, CacheListCallable callable, Set<T> defaultValue);

    boolean removeFromZSet(String key, Object value);

    boolean hasKey(String key);

    boolean expire(String key, long timeout, TimeUnit unit);

    void delete(String key);

    void addToMap(String key, Map map);

    <K, V> Map<K, V> getMap(String key);

    boolean addToGeo(String key, double x, double y, String member);

    boolean removeFromGeo(String key, String... members);

    Distance distanceGeo(String key, String member1, String member2);

    GeoResults<RedisGeoCommands.GeoLocation<String>> radiusGeo(String key, double x, double y,
                                                               double distance, RedisGeoCommands.DistanceUnit distanceUnit, Sort.Direction sortOrder, long count);

    boolean setBit(String key, int offset, boolean value);

    byte[] getBit(String key);

    <T> T lock(String key, long time, TimeUnit timeUnit, MutexProcessWithReturn<T> processWithReturn) throws LockException;

}
