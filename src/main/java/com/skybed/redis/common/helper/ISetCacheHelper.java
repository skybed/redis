package com.skybed.redis.common.helper;

import java.util.Set;

public interface ISetCacheHelper {

	/**
	  * 添加数据到Set缓存
	 * @param value
	 * @return
	 */
    public boolean add(String value);

    /**
	  * 从Set缓存移除value
     * @param value
     */
    public void remove(String value);
    
    /**
	  * 查出table下的Set数据列表
     * @return
     */
    public Set<String> keySet();

    /**
   	  * 判断Set缓存是否包含value
     * @param value
     * @return
     */
    public boolean contains(String value);

    /**
	  * 清空table中所有数据
     * @return
     */
    public void delTable();

}
