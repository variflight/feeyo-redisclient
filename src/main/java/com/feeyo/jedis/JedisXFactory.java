package com.feeyo.jedis;

import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.exceptions.JedisException;

/**
 * PoolableObjectFactory custom impl.
 */
public class JedisXFactory implements PooledObjectFactory<JedisX> {
	
	private final AtomicReference<HostAndPort> hostAndPort = new AtomicReference<HostAndPort>();
	private final int connectionTimeout;
	private final int soTimeout;
	private final String password;

	public JedisXFactory(final String host, final int port, final int connectionTimeout, final int soTimeout,
			final String password) {
		this.hostAndPort.set(new HostAndPort(host, port));
		this.connectionTimeout = connectionTimeout;
		this.soTimeout = soTimeout;
		this.password = password;
	}

	public void setHostAndPort(final HostAndPort hostAndPort) {
		this.hostAndPort.set(hostAndPort);
	}

	public void activateObject(PooledObject<JedisX> pooledJedis) throws Exception {
	}

	public void destroyObject(PooledObject<JedisX> pooledJedis) throws Exception {
		final JedisX jedis = pooledJedis.getObject();
		if (jedis.isConnected()) {
			try {
				try {
					jedis.quit();
				} catch (Exception e) {
				}
				jedis.close();
			} catch (Exception e) {

			}
		}

	}

	public PooledObject<JedisX> makeObject() throws Exception {
		final HostAndPort hostAndPort = this.hostAndPort.get();

		final JedisX jedis = new JedisX(hostAndPort.getHost(), hostAndPort.getPort(), connectionTimeout,
				soTimeout);

		try {
			jedis.connect();
			if (password != null) {
				jedis.auth(password);
			}
		} catch (JedisException je) {
			jedis.close();
			throw je;
		}

		return new DefaultPooledObject<JedisX>(jedis);

	}

	public void passivateObject(PooledObject<JedisX> pooledJedis) throws Exception {
		// TODO maybe should select db 0? Not sure right now.
	}

	public boolean validateObject(PooledObject<JedisX> pooledJedis) {
		final JedisX jedis = pooledJedis.getObject();
		try {
			HostAndPort hostAndPort = this.hostAndPort.get();

			String connectionHost = jedis.getClient().getHost();
			int connectionPort = jedis.getClient().getPort();

			return hostAndPort.getHost().equals(connectionHost) && hostAndPort.getPort() == connectionPort
					&& jedis.isConnected() && jedis.ping().equals("PONG");
		} catch (final Exception e) {
			return false;
		}
	}
}