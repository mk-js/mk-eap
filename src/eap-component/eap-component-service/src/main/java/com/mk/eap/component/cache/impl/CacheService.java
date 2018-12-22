package com.mk.eap.component.cache.impl;

import com.mk.eap.component.cache.itf.ICacheService;
import com.mk.eap.component.cache.redis.impl.StringRedisDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.alibaba.dubbo.config.annotation.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;


@Component
@Service
public class CacheService implements ICacheService {

	@Autowired
	private StringRedisDao stringRedisDao;


	@Override
	public boolean addObject(String key, Object object, Long timeout, Class<?> clazz) throws ClassCastException {
		return stringRedisDao.addObject(key, object, timeout, clazz);
	}

	@Override
	public Object getObject(String key) {
		return stringRedisDao.getObject(key);
	}

	@Override
	public boolean addString(String key, String value, Long timeout) {
		return stringRedisDao.addString(key, value, timeout);
	}

	@Override
	public String getString(String key) {
		return stringRedisDao.getString(key);
	}

	@Override
	public boolean updateString(final String key, final String value){
		return stringRedisDao.updateString(key,value);
	}

	@Override
	public boolean addHash(String key, String field, String value, Long timeout) {
		return stringRedisDao.addHash(key, field, value, timeout);
	}

	@Override
	public boolean addHash(String key, Map<String, String> fieldValueList, Long timeout) {
		return stringRedisDao.addHash(key, fieldValueList, timeout);
	}

	@Override
	public Object getHashField(String key, String field) {
		return stringRedisDao.getHashField(key, field);
	}

	@Override
	public Map<byte[], byte[]> getHashAll(String key, String field) {
		return stringRedisDao.getHashAll(key, field);
	}

	@Override
	public void delete(String key) {
		stringRedisDao.delete(key);
	}

	@Override
	public Long push(String key, String value) {
		return stringRedisDao.push(key, value);
	}

	@Override
	public String pop(String key) {
		return stringRedisDao.pop(key);
	}

	@Override
	public Long in(String key, String value) {
		return stringRedisDao.in(key,value);
	}

	@Override
	public String out(String key) {
		return stringRedisDao.out(key);
	}

	@Override
	public Long length(String key) {
		return stringRedisDao.length(key);
	}

	@Override
	public List<Object> range(String key, int start, int end) {
		return stringRedisDao.range(key, start, end);
	}

	@Override
	public void remove(String key, long i, String value) {
		stringRedisDao.remove(key, i, value);
	}

	@Override
	public String index(String key, long index) {
		return stringRedisDao.index(key, index);
	}

	@Override
	public void set(String key, long index, String value) {
		stringRedisDao.set(key, index, value);
	}

	@Override
	public void trim(String key, long start, int end) {
		stringRedisDao.trim(key, start, end);
	}

	@Override
	public Long addSet(String key, String value, Long timeout) {
		return stringRedisDao.addSet(key, value, timeout);
	}

	@Override
	public Set<byte[]> getSet(String key) {
		return stringRedisDao.getSet(key);
	}
}