package com.skybed.redis.common.util;

import java.util.Set;

import org.apache.log4j.Logger;

import com.skybed.redis.common.helper.ISetCacheHelper;

import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.ShardedJedis;
import redis.clients.util.Pool;

/**
 * redis缓存 存储类型为Set
 *
 */
@SuppressWarnings("deprecation")
public class RedisSetCacheHelper implements ISetCacheHelper {
	
	private String table = null;

	public RedisSetCacheHelper(String table){
		this.table = table;
	}
	
	public boolean add(String value){
		Pool<ShardedJedis> curjedisPool = RedisManager.getInstance().getJedisPool();
		ShardedJedis jedis = null;
		JedisCluster jedisCluster = null;
		try {
			if (null == curjedisPool) {
				jedisCluster = RedisManager.getInstance().getJedisCluster();
				jedisCluster.sadd(table, value);
				return true;
			}
			jedis = curjedisPool.getResource();
			jedis.sadd(table, value);

			return true;
		} catch (Exception e) {
			Logger.getRootLogger().error("RedisSetCacheHelper-add table:" + table + " value:" + value + " throw Exception:" + e.getMessage(), e);
		} finally {
			if (jedis != null) {
				curjedisPool.returnResource(jedis);
			}
		}
		return false;
	}

	public void remove(String value){
		Pool<ShardedJedis> curjedisPool = RedisManager.getInstance().getJedisPool();
		ShardedJedis jedis = null;
		JedisCluster jedisCluster = null;
		try {
			if (null == curjedisPool) {
				jedisCluster = RedisManager.getInstance().getJedisCluster();
				jedisCluster.srem(table, value);
			}
			jedis = curjedisPool.getResource();
			jedis.srem(table, value);
		} catch (Exception e) {
			Logger.getRootLogger().error("RedisSetCacheHelper-remove table:" + table + " value:" + value + " throw Exception:" + e.getMessage(), e);
		} finally {
			if (jedis != null) {
				curjedisPool.returnResource(jedis);
			}
		}
	}

	public Set<String> keySet() {
		Pool<ShardedJedis> curjedisPool = RedisManager.getInstance().getJedisPool();
		ShardedJedis jedis = null;
		JedisCluster jedisCluster = null;
		try {
			if (null == curjedisPool) {
				jedisCluster = RedisManager.getInstance().getJedisCluster();
				return jedisCluster.smembers(table);
			}
			jedis = curjedisPool.getResource();
			return jedis.smembers(table);
		} catch (Exception e) {
			Logger.getRootLogger().error("RedisSetCacheHelper-keySet table:" + table + " throw Exception:" + e.getMessage(), e);
		} finally {
			if (jedis != null) {
				curjedisPool.returnResource(jedis);
			}
		}
		return null;
	}

	public boolean contains(String value) {
		return keySet().contains(value);
	}

	@Override
	public void delTable() {
		Pool<ShardedJedis> curjedisPool = RedisManager.getInstance().getJedisPool();
		ShardedJedis jedis = null;
		JedisCluster jedisCluster = null;
		try {
			if (null == curjedisPool) {
				jedisCluster = RedisManager.getInstance().getJedisCluster();
				Set<String> keys = jedisCluster.smembers(table);
				for (String key : keys) {
					jedisCluster.srem(table, key);
				}
			} else {
				jedis = curjedisPool.getResource();
				jedis.del(table);
			}
			Logger.getRootLogger().info("RedisMapCacheHelper-flush table:" + table + " success!");
		} catch (Exception e) {
			Logger.getRootLogger().error("RedisMapCacheHelper-flush table:" + table + " throw Exception:" + e.getMessage(), e);
		} finally {
			if (jedis != null) {
				curjedisPool.returnResource(jedis);
			}
		}
	}

}

