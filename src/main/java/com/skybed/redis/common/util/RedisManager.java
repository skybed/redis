package com.skybed.redis.common.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.log4j.Logger;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.util.Pool;

public class RedisManager {

	private static RedisManager instance;
	private ShardedJedisPool jedisPool;
	private ShardedJedisSentinelPool sentinelPool;
	private JedisCluster jedisCluster;
	
	private String redisType = null;
	
	private Integer TIMEOUT = 60000;
	
	public static RedisManager getInstance() {
		synchronized (RedisManager.class) {
			if (instance == null) {
				instance = new RedisManager();
			}
		}
		return instance;
	}

	public Pool<ShardedJedis> getJedisPool() {
		if("redis-sentinel".equals(redisType)) {
			return sentinelPool;
		}else if("redis-cluster".equals(redisType)) {
			return null;
		} else{
			return jedisPool;
		}
	}

	public void initRedisPool(String type, String masterNames, String connectAddr, String password) {
		try {
			redisType = type;
			if("redis-sentinel".equals(type)) {//redis哨兵模式
				GenericObjectPoolConfig gPoolConfig=new GenericObjectPoolConfig();
				gPoolConfig.setMaxIdle(10); 
				gPoolConfig.setMaxTotal(50); 
				gPoolConfig.setMaxWaitMillis(1000);
				gPoolConfig.setJmxEnabled(true);
			    
				String[] masterStr = masterNames.split(",");
				List<String> masters = Arrays.asList(masterStr);
				
				String[] addrs = connectAddr.split(",");
			    Set<String> sentinels = new HashSet<String>(Arrays.asList(addrs));
			    
			    if(!StringUtils.isEmpty(password)) {
			    	sentinelPool = new ShardedJedisSentinelPool(masters, sentinels, gPoolConfig, TIMEOUT, password);
				} else {
					sentinelPool = new ShardedJedisSentinelPool(masters, sentinels, gPoolConfig, TIMEOUT);
				}
			    Logger.getRootLogger().info("init redis pool mode:redis-sentinel, address :" + connectAddr + " success!");
			} else if("redis-cluster".equals(type)) {//redis集群模式
				// 初始化对象池配置
				GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
				poolConfig.setBlockWhenExhausted(true);
				poolConfig.setMaxWaitMillis(1000);
				poolConfig.setMinIdle(0);
				poolConfig.setMaxIdle(8);
				poolConfig.setMaxTotal(8);
				poolConfig.setTestOnBorrow(true);
				poolConfig.setTestOnReturn(true);
				poolConfig.setTestOnCreate(true);
				poolConfig.setTestWhileIdle(true);
				poolConfig.setLifo(true);
				
				Set<HostAndPort> hostAndPortsSet = new HashSet<HostAndPort>();
				
				if (!StringUtils.isEmpty(connectAddr)) {
					String[] addrs = connectAddr.split(",");
					Set<String> sentinels = new HashSet<String>(Arrays.asList(addrs));
					for (String sentinel : sentinels) {
						String sentinelHost = sentinel.split(":")[0].trim();
						int sentinelPort = Integer.parseInt(sentinel.split(":")[1].trim());
						HostAndPort hostAndPort = new HostAndPort(sentinelHost, sentinelPort);
						hostAndPortsSet.add(hostAndPort);
					}
				}
				
				if(!StringUtils.isEmpty(password)) {
					jedisCluster = new JedisCluster(hostAndPortsSet, 1000, 1000, 3, password, poolConfig);
				} else {
					jedisCluster = new JedisCluster(hostAndPortsSet, 1000, 1000, 3, poolConfig);
				}
				
				Logger.getRootLogger().info("init redis pool mode:redis-cluster, address :" + connectAddr + " success!");
			} else {//redis单点
				JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
				jedisPoolConfig.setMaxTotal(50);
				jedisPoolConfig.setMaxIdle(10);
				jedisPoolConfig.setMaxWaitMillis(1000);
				jedisPoolConfig.setTestOnBorrow(true);
				
				String ip = connectAddr.split(":")[0];
				Integer port = Integer.parseInt(connectAddr.split(":")[1]);
				JedisShardInfo jedisShard = new JedisShardInfo(ip, port, TIMEOUT);
				
				if(!StringUtils.isEmpty(password)){
					jedisShard.setPassword(password);
				}
				List<JedisShardInfo> jedisShardList = new ArrayList<JedisShardInfo>();
				jedisShardList.add(jedisShard);
				jedisPool = new ShardedJedisPool(jedisPoolConfig, jedisShardList);
				
				Logger.getRootLogger().info("init redis pool mode:redis, address :" + connectAddr + " success!");
			}
		} catch (Exception e) {
			Logger.getRootLogger().error("init redis pool throw Exception:" + e.getMessage(), e);
		}
	}
	
	
	public JedisCluster getJedisCluster() {
		return jedisCluster;
	}
	
}
