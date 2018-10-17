package com.skybed.redis.common.base;

import com.skybed.redis.common.enums.CacheType;
import com.skybed.redis.common.helper.MapCacheHelper;

public abstract class AbsMapCacher implements IMapCache {
	
	private MapCacheHelper mapCache = null;
	
	protected AbsMapCacher() {}
	
	@SuppressWarnings("unused")
	private AbsMapCacher getCacheInstance(CacheType curType) {
		return null;
	}
	
	protected AbsMapCacher(String instanceName) {
		this.mapCache = new MapCacheHelper(instanceName + "_cache");
	}

	@Override
	public void save(String key, String value) {
		mapCache.put(key, value);
	}

	@Override
	public void updateByKey(String key, String value) {
		if(mapCache.containsKey(key)) {
			mapCache.remove(key);
		}
		mapCache.put(key, value);
	}

	@Override
	public void deleteByKey(String key) {
		if(mapCache.containsKey(key)) {
			mapCache.remove(key);
		}
	}

	@Override
	public String findByKey(String key) {
		if(mapCache.containsKey(key)) {
			return mapCache.get(key);
		}
		return null;
	}

	@Override
	public boolean containsKey(String key) {
		if(mapCache.containsKey(key)){
			return true;
		}
		return false;
	}

	@Override
	public void flushTable() {
		mapCache.delTable();
	}
	
}
