package com.skybed.redis.common.helper;

import java.util.Set;

import com.skybed.redis.common.util.RedisSetCacheHelper;

public class SetCacheHelper implements ISetCacheHelper {

	private ISetCacheHelper setCacheHelper;

	public SetCacheHelper(String table){
        setCacheHelper = new RedisSetCacheHelper(table);
    }

	@Override
    public boolean add(String value) {
        return setCacheHelper.add(value);
    }

	@Override
    public void remove(String value) {
        setCacheHelper.remove(value);
    }
	
	@Override
    public Set<String> keySet() {
        return setCacheHelper.keySet();
    }

	@Override
    public boolean contains(String value) {
        return setCacheHelper.contains(value);
    }

    @Override
    public void delTable() {
        setCacheHelper.delTable();
    }

}
