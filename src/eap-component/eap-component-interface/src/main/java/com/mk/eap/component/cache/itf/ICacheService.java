package com.mk.eap.component.cache.itf;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ICacheService {

	// ----------------------------------------Object----------------------------------------
	/**
	 * 设置对象
	 * 
	 * @param key
	 * @param object
	 * @param timeout
	 * @param 对象class
	 * @return
	 * @throws Exception
	 */
	public boolean addObject(String key, Object object, Long timeout, Class<?> clazz) throws ClassCastException;

	/**
	 * 获得对象
	 * 
	 * @param key
	 * @return
	 */
	public Object getObject(String key);

	/**
	 * 新增String ----setNX 不存在则增加 ------------------------------
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @param timout
	 *            超时(秒)
	 * @return true 操作成功，false 已存在值
	 */
	boolean addString(String key, String value, Long timeout);

	/**
	 * 批量新增String---setNx 不存在则增加
	 * 
	 * @param keyValueList
	 *            键值对的map
	 * @param timeout
	 *            超时处理
	 * @return
	 */
	//boolean addString(Map<String, String> keyValueList, Long timeout);

	/**
	 * 通过key获取单个
	 * 
	 * @param key
	 * @return
	 */
	String getString(String key);

	/**
	 * 修改 String
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean updateString(final String key, final String value);
	
	// ---------------------------------------List-----------------------------------------
	/**
	 * 新增Hash ----setNX 不存在则增加 ------------------------------
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @param timout
	 *            超时(秒)
	 * @return true 操作成功，false 已存在值
	 */
	boolean addHash(String key, String field, String value, Long timeout);

	/**
	 * 批量新增Hash ----setNX 不存在则增加 ------------------------------
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @param timout
	 *            超时(秒)
	 * @return true 操作成功，false 已存在值
	 */
	boolean addHash(String key, Map<String, String> fieldValueList, Long timeout);

	/**
	 * 通过key获取单个
	 * 
	 * @param key
	 * @return
	 */
	Object getHashField(String key, String field);

	/**
	 * 通过key获取整个Hash
	 * 
	 * @param key
	 * @return
	 */
	Map<byte[], byte[]> getHashAll(String key, String field);

	//---------------------------------------------------通用删除-------------------------------------------------
	/**
	 * 删除单个
	 * 
	 * @param key
	 */
	public void delete(String key);

	//----------------------------------------------------队列操作--------------------------------------------------
	/** 
	* 压栈 
	*  
	* @param key 
	* @param value 
	* @return 
	*/
	Long push(String key, String value);

	/** 
	 * 出栈 
	 *  
	 * @param key 
	 * @return 
	 */
	String pop(String key);

	/** 
	 * 入队 
	 *  
	 * @param key 
	 * @param value 
	 * @return 
	 */
	Long in(String key, String value);

	/** 
	 * 出队 
	 *  
	 * @param key 
	 * @return 
	 */
	String out(String key);

	/** 
	 * 栈/队列长 
	 *  
	 * @param key 
	 * @return 
	 */
	Long length(String key);

	/** 
	 * 范围检索 
	 *  
	 * @param key 
	 * @param start 
	 * @param end 
	 * @return 
	 */
	List<Object> range(String key, int start, int end);

	/** 
	 * 移除 
	 *  
	 * @param key 
	 * @param i 
	 * @param value 
	 */
	void remove(String key, long i, String value);

	/** 
	 * 检索 
	 *  
	 * @param key 
	 * @param index 
	 * @return 
	 */
	String index(String key, long index);

	/** 
	 * 置值 
	 *  
	 * @param key 
	 * @param index 
	 * @param value 
	 */
	void set(String key, long index, String value);

	/** 
	 * 裁剪 
	 *  
	 * @param key 
	 * @param start 
	 * @param end 
	 */
	void trim(String key, long start, int end);

	//---------------------------------------------------SET-----------------------------------------------
	/**
	 * 新增Set ----setNX 不存在则增加 ------------------------------
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @param timout
	 *            超时(秒)
	 * @return true 操作成功，false 已存在值
	 */
	Long addSet(String key, String value, Long timeout);

	/**
	 * 通过key获取单个Set
	 * 
	 * @param key
	 * @return
	 */
	Set<byte[]> getSet(String key);

}