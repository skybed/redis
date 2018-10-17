package com.skybed.redis.common.helper;

import java.util.Map;
import java.util.Set;

import com.skybed.redis.common.util.RedisMapCacheHelper;

public class MapCacheHelper implements IMapCacheHelper {

	private IMapCacheHelper mapCacheHelper;

	public MapCacheHelper(String table){
        mapCacheHelper = new RedisMapCacheHelper(table);
    }

	@Override
    public String get(String key) {
        return mapCacheHelper.get(key);
    }

	@Override
    public boolean put(String key, String value) {
        return mapCacheHelper.put(key, value);
    }

	@Override
    public void remove(String key) {
        mapCacheHelper.remove(key);
    }

	@Override
    public Set<String> keySet() {
        return mapCacheHelper.keySet();
    }

	@Override
    public Set<Map.Entry<String, String>> entrySet() {
        return mapCacheHelper.entrySet();
    }

    @Override
    public boolean containsKey(String key) {
        return mapCacheHelper.containsKey(key);
    }

    @Override
    public void delTable() {
        mapCacheHelper.delTable();
    }

}
