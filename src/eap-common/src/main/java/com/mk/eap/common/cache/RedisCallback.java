package com.mk.eap.common.cache;

import org.springframework.data.redis.connection.RedisConnection;

/**
 * @author gaoxue
 */
public interface RedisCallback {

    Object doWithRedis(RedisConnection connection);
}
