package com.mk.eap.common.cache;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.ibatis.cache.Cache;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * MyBatis cache adapter for Redis
 * @author gaoxue
 */
public class MyBatisRedisCache implements Cache {

    private static JedisConnectionFactory jedisConnectionFactory;

    private static RedisSerializer<Object> serializer = new JdkSerializationRedisSerializer();

    /** Cache identifier, mapper namespace */
    private final String id;

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public MyBatisRedisCache(final String id) {
        if (id == null) {
            throw new IllegalArgumentException("Cache instances require an ID");
        }
        this.id = id;
    }

    private Object excute(RedisCallback callback) {
        RedisConnection connection = null;
        try {
            connection = jedisConnectionFactory.getConnection();
            return callback.doWithRedis(connection);
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void putObject(Object key, Object value) {
        excute(new RedisCallback() {
            @Override
            public Object doWithRedis(RedisConnection connection) {
                connection.hSet(serializer.serialize(id), serializer.serialize(key), serializer.serialize(value));
                return null;
            }
        });
    }

    @Override
    public Object getObject(Object key) {
        return excute(new RedisCallback() {
            @Override
            public Object doWithRedis(RedisConnection connection) {
                return serializer.deserialize(connection.hGet(serializer.serialize(id), serializer.serialize(key)));
            }
        });
    }


    @Override
    public Object removeObject(Object key) {
        return excute(new RedisCallback() {
            @Override
            public Object doWithRedis(RedisConnection connection) {
                connection.hDel(serializer.serialize(id), serializer.serialize(key));
                return null;
            }
        });
    }

    @Override
    public void clear() {
        excute(new RedisCallback() {
            @Override
            public Object doWithRedis(RedisConnection connection) {
                connection.del(serializer.serialize(id));
                return null;
            }
        });
    }

    @Override
    public int getSize() {
        Object size = excute(new RedisCallback() {
            @Override
            public Object doWithRedis(RedisConnection connection) {
                return connection.hLen(serializer.serialize(id));
            }
        });
        return Integer.parseInt(size.toString());
    }

    @Override
    public ReadWriteLock getReadWriteLock() {
        return readWriteLock;
    }

    public static void setJedisConnectionFactory(JedisConnectionFactory jedisConnectionFactory) {
        MyBatisRedisCache.jedisConnectionFactory = jedisConnectionFactory;
    }

}