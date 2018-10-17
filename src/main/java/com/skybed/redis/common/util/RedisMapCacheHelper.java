package com.skybed.redis.common.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import com.skybed.redis.common.helper.IMapCacheHelper;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.ShardedJedis;
import redis.clients.util.Pool;

@SuppressWarnings("deprecation")
public class RedisMapCacheHelper implements IMapCacheHelper {

	private String table = null;

	public RedisMapCacheHelper(String table) {
		this.table = table;
	}

	public String get(String key) {
		Pool<ShardedJedis> curjedisPool = RedisManager.getInstance().getJedisPool();
		ShardedJedis jedis = null;
		JedisCluster jedisCluster = null;
		try {
			if (null == curjedisPool) {
				jedisCluster = RedisManager.getInstance().getJedisCluster();
				return jedisCluster.hget(table, key);
			}
			jedis = curjedisPool.getResource();
			return jedis.hget(table, key);
		} catch (Exception e) {
			Logger.getRootLogger().error("RedisMapCacheHelper-get table:" + table + " key" + key + " throw Exception:" + e.getMessage(), e);
		} finally {
			if (jedis != null) {
				curjedisPool.returnResource(jedis);
			}
		}
		return null;
	}

	public boolean put(String key, String value) {
		Pool<ShardedJedis> curjedisPool = RedisManager.getInstance().getJedisPool();
		ShardedJedis jedis = null;
		JedisCluster jedisCluster = null;
		try {
			if (null == curjedisPool) {
				jedisCluster = RedisManager.getInstance().getJedisCluster();
				jedisCluster.hset(table, key, value);
				return true;
			}
			jedis = curjedisPool.getResource();
			jedis.hset(table, key, value);
			return true;
		} catch (Exception e) {
			Logger.getRootLogger().error("RedisMapCacheHelper-put table:" + table + " key:" + key + " value:" + value + " throw Exception:" + e.getMessage(), e);
		} finally {
			if (jedis != null) {
				curjedisPool.returnResource(jedis);
			}
		}
		return false;
	}

	public void remove(String key) {
		Pool<ShardedJedis> curjedisPool = RedisManager.getInstance().getJedisPool();
		ShardedJedis jedis = null;
		JedisCluster jedisCluster = null;
		try {
			if (null == curjedisPool) {
				jedisCluster = RedisManager.getInstance().getJedisCluster();
				jedisCluster.hdel(table, key);
				return;
			}
			jedis = curjedisPool.getResource();
			jedis.hdel(table, key);
		} catch (Exception e) {
			Logger.getRootLogger().error("RedisMapCacheHelper-remove table:" + table + " key:" + key + " throw Exception:" + e.getMessage(), e);
		} finally {
			if (jedis != null) {
				curjedisPool.returnResource(jedis);
			}
		}
	}

	public Set<String> keySet() {
		Set<String> results = new HashSet<String>();
		Pool<ShardedJedis> curjedisPool = RedisManager.getInstance().getJedisPool();
		ShardedJedis shardedJedis = null;
		JedisCluster jedisCluster = null;
		try {
			if (null == curjedisPool) {
				jedisCluster = RedisManager.getInstance().getJedisCluster();
				return jedisCluster.hkeys(table);
			}
			shardedJedis = curjedisPool.getResource();

			return shardedJedis.hkeys(table);
		} catch (Exception e) {
			Logger.getRootLogger().error("RedisMapCacheHelper-keySet table:" + table + " throw Exception:" + e.getMessage(), e);
		} finally {
			if (shardedJedis != null) {
				curjedisPool.returnResource(shardedJedis);
			}
		}
		return results;
	}

	@SuppressWarnings("unused")
	private Set<String> getAllkeys(JedisCluster jedisCluster){
		Set<String> keys = new TreeSet<String>();
		if (table != null) {
			return  jedisCluster.hkeys(table);
		}
		
		Map<String, JedisPool> clusterNodes = jedisCluster.getClusterNodes();
		for (Entry<String, JedisPool> entry : clusterNodes.entrySet()) {
			JedisPool pool = entry.getValue();
			Jedis jedis = pool.getResource();
			try {
				keys.addAll(jedis.keys("*"));
			} catch (Exception e) {
				Logger.getRootLogger().error("获取keys发生异常！ : throw Exception:" + e.getMessage(), e);
			} finally {
				jedis.close();
			}
		}
		return keys;
	}

	public Set<Map.Entry<String, String>> entrySet() {
		Map<String, String> results = new HashMap<String, String>();
		Pool<ShardedJedis> curjedisPool = RedisManager.getInstance().getJedisPool();
		ShardedJedis jedis = null;
		JedisCluster jedisCluster = null;
		try {
			if (null == curjedisPool) {
				jedisCluster = RedisManager.getInstance().getJedisCluster();
				Set<String> keys = jedisCluster.hkeys(table);
				if (keys != null && keys.size() > 0) {
					for (String key : keys) {
						String value = jedisCluster.get(key);
						if (value != null) {
							results.put(key, value);
						}
					}
				}
				return results.entrySet();
			}
			
			jedis = curjedisPool.getResource();
			Set<String> keys = jedis.hkeys(table);
			if (keys != null && keys.size() > 0) {
				for (String key : keys) {
					String value = jedis.hget(table, key);

					if (value != null) {
						results.put(key, value);
					}
				}
			}
			return results.entrySet();
		} catch (Exception e) {
			Logger.getRootLogger().error("RedisMapCacheHelper-entrySet table:" + table + " throw Exception:" + e.getMessage(), e);
		} finally {
			if (jedis != null) {
				curjedisPool.returnResource(jedis);
			}
		}
		return null;
	}

	public boolean containsKey(String key) {
		Pool<ShardedJedis> curjedisPool = RedisManager.getInstance().getJedisPool();
		ShardedJedis jedis = null;
		JedisCluster jedisCluster = null;
		try {
			if (null == curjedisPool) {
				jedisCluster = RedisManager.getInstance().getJedisCluster();
				return jedisCluster.hexists(table,key);
			}
			
			jedis = curjedisPool.getResource();
			return jedis.hexists(table, key);
		} catch (Exception e) {
			Logger.getRootLogger().error("RedisMapCacheHelper-containsKey table:" + table + " key:" + key + " throw Exception:" + e.getMessage(), e);
		} finally {
			if (jedis != null) {
				curjedisPool.returnResource(jedis);
			}
		}
		return false;
	}

	@Override
	public void delTable() {
		Pool<ShardedJedis> curjedisPool = RedisManager.getInstance().getJedisPool();
		ShardedJedis jedis = null;
		JedisCluster jedisCluster = null;
		try {
			if (null == curjedisPool) {
				jedisCluster = RedisManager.getInstance().getJedisCluster();
				Set<String> keys = jedisCluster.hkeys(table);
				for (String key : keys) {
					jedisCluster.hdel(table, key);
				}
				return;
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
