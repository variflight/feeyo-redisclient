package com.feeyo.jedis;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import redis.clients.jedis.exceptions.JedisException;
import redis.clients.util.Pool;

public class JedisXPool extends Pool<JedisX> {
	
	public JedisXPool(final GenericObjectPoolConfig poolConfig, final String host, int port, int timeout,
			final String password) {
		super(poolConfig, new JedisXFactory(host, port, timeout, timeout, password));
	}

	@Override
	public JedisX getResource() {
		JedisX jedis = super.getResource();
		jedis.setDataSource( this );
		return jedis;
	}


	@Override
	@Deprecated
	public void returnBrokenResource(final JedisX resource) {
		if (resource != null) {
			returnBrokenResourceObject(resource);
		}
	}

	@Override
	@Deprecated
	public void returnResource(final JedisX resource) {
		if (resource != null) {
			try {
				returnResourceObject(resource);
			} catch (Exception e) {
				returnBrokenResource(resource);
				throw new JedisException("Could not return the resource to the pool", e);
			}
		}
	}}
