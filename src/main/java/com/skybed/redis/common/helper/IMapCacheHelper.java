package com.skybed.redis.common.helper;

import java.util.Map;
import java.util.Set;

public interface IMapCacheHelper {
 
	/**
	 * 从redis的Map类型缓存中查询key对应的值
	 * @param key
	 * @return
	 */
    public String get(String key);

    /**
	* 保存数据到redis的Map类型缓存中
     * @param key
     * @param value
     * @return
     */
    public boolean put(String key, String value);

    /**
	* 从redis的Map类型缓存中删除key对应的值
     * @param key
     */
    public void remove(String key);
    
    /**
	* 获取redis的Map类型缓存中所有key值
     * @return
     */
    public Set<String> keySet();

    /**
	* 获取redis的Map类型缓存中所有数据
     * @return
     */
    public Set<Map.Entry<String, String>> entrySet();

    /**
	* 判断redis的Map类型缓存中是否存在key值
     * @param key
     * @return
     */
    public boolean containsKey(String key);

    /**
	* 清空table中所有数据
     * @return
     */
    public void delTable();

}
