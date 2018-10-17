package com.skybed.redis.common.base;

public interface IMapCache {

	/**
	 * 保存数据到Map缓存
	 * @param key
	 * @param value
	 */
	public void save(String key, String value);
	
	/**
	 * 通过key更新缓存
	 * @param key
	 * @param value
	 */
	public void updateByKey(String key, String value);
	
	/**
	 * 通过key移除缓存
	 * @param key
	 */
	public void deleteByKey(String key);
	
	/**
	 * 通过key找到缓存数据
	 * @param key
	 * @return
	 */
	public String findByKey(String key);

	/**
	 * 是否包含指定的key
	 * @param key
	 * @return
	 */
	public boolean containsKey(String key);

	/**
	 * 删除指定缓存
	 * @return
	 */
	public void flushTable();

	
}
