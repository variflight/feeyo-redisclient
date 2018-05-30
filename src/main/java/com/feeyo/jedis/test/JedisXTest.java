package com.feeyo.jedis.test;

import com.feeyo.jedis.JedisX;
import com.feeyo.jedis.JedisXPool;

import redis.clients.jedis.JedisPoolConfig;

public class JedisXTest {
	public static void main(String[] args) throws InterruptedException {
		// 连接池中最大空闲的连接数
		int maxIdle = 100;
		int minIdle = 20;

		// 当调用borrow Object方法时，是否进行有效性检查
		boolean testOnBorrow = false;

		// 当调用return Object方法时，是否进行有效性检查
		boolean testOnReturn = false;

		// 如果为true，表示有一个idle object evitor线程对idle
		// object进行扫描，如果validate失败，此object会被从pool中drop掉
		// TODO: 这一项只有在timeBetweenEvictionRunsMillis大于0时才有意义
		boolean testWhileIdle = true;

		// 对于“空闲链接”检测线程而言，每次检测的链接资源的个数.(jedis 默认设置成-1)
		int numTestsPerEvictionRun = -1;

		// 连接空闲的最小时间，达到此值后空闲连接将可能会被移除。负值(-1)表示不移除
		int minEvictableIdleTimeMillis = 60 * 1000;

		// “空闲链接”检测线程，检测的周期，毫秒数。如果为负值，表示不运行“检测线程”。默认为-1
		int timeBetweenEvictionRunsMillis = 30 * 1000;

		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
		jedisPoolConfig.setMaxIdle(maxIdle);
		jedisPoolConfig.setMinIdle(minIdle);
		jedisPoolConfig.setTestOnBorrow(testOnBorrow);
		jedisPoolConfig.setTestOnReturn(testOnReturn);
		jedisPoolConfig.setTestWhileIdle(testWhileIdle);

		jedisPoolConfig.setNumTestsPerEvictionRun(numTestsPerEvictionRun);
		jedisPoolConfig.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
		jedisPoolConfig.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);

		JedisXPool jedisXPool = new JedisXPool(jedisPoolConfig, "127.0.0.1", 8066, 30000, "tod_yt_kfktest_fdafd4809kimchgdfh");
		while (true) {
			JedisX jedisX = null;
			try {
				jedisX = jedisXPool.getResource();
				System.out.println(jedisX.kpush("test01", "111"));
				System.out.println(jedisX.kpush("test01", "111", 2));
				System.out.println(jedisX.kpop("test01"));
				System.out.println(jedisX.kpop("test01", 1, 0));
				System.out.println(jedisX.kpartitions("test01"));
				System.out.println(jedisX.koffset("test01", "0", "-1"));
			} catch (Exception e) {
				
			} finally {
				if (jedisX != null)
					jedisX.close();
			}
		}
		
	}
}
