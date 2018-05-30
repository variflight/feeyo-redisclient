package com.feeyo.jedis;

import java.net.URI;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocketFactory;

import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.exceptions.InvalidURIException;
import redis.clients.util.JedisURIHelper;
import redis.clients.util.Pool;

public class JedisX {
	
	protected ClientX clientx = null;
	protected Pool<JedisX> dataSource = null;

	public JedisX() {
		clientx = new ClientX();
	}

	public JedisX(final String host) {
		URI uri = URI.create(host);
		if (uri.getScheme() != null && (uri.getScheme().equals("redis") || uri.getScheme().equals("rediss"))) {
			initializeClientFromURI(uri);
		} else {
			clientx = new ClientX(host);
		}
	}

	public JedisX(final String host, final int port) {
		clientx = new ClientX(host, port);
	}

	public JedisX(final String host, final int port, final boolean ssl) {
		clientx = new ClientX(host, port, ssl);
	}

	public JedisX(final String host, final int port, final boolean ssl, final SSLSocketFactory sslSocketFactory,
			final SSLParameters sslParameters, final HostnameVerifier hostnameVerifier) {
		clientx = new ClientX(host, port, ssl, sslSocketFactory, sslParameters, hostnameVerifier);
	}

	public JedisX(final String host, final int port, final int timeout) {
		clientx = new ClientX(host, port);
		clientx.setConnectionTimeout(timeout);
		clientx.setSoTimeout(timeout);
	}

	public JedisX(final String host, final int port, final int timeout, final boolean ssl) {
		clientx = new ClientX(host, port, ssl);
		clientx.setConnectionTimeout(timeout);
		clientx.setSoTimeout(timeout);
	}

	public JedisX(final String host, final int port, final int timeout, final boolean ssl,
			final SSLSocketFactory sslSocketFactory, final SSLParameters sslParameters,
			final HostnameVerifier hostnameVerifier) {
		clientx = new ClientX(host, port, ssl, sslSocketFactory, sslParameters, hostnameVerifier);
		clientx.setConnectionTimeout(timeout);
		clientx.setSoTimeout(timeout);
	}

	public JedisX(final String host, final int port, final int connectionTimeout, final int soTimeout) {
		clientx = new ClientX(host, port);
		clientx.setConnectionTimeout(connectionTimeout);
		clientx.setSoTimeout(soTimeout);
	}

	public JedisX(final String host, final int port, final int connectionTimeout, final int soTimeout,
			final boolean ssl) {
		clientx = new ClientX(host, port, ssl);
		clientx.setConnectionTimeout(connectionTimeout);
		clientx.setSoTimeout(soTimeout);
	}

	public JedisX(final String host, final int port, final int connectionTimeout, final int soTimeout,
			final boolean ssl, final SSLSocketFactory sslSocketFactory, final SSLParameters sslParameters,
			final HostnameVerifier hostnameVerifier) {
		clientx = new ClientX(host, port, ssl, sslSocketFactory, sslParameters, hostnameVerifier);
		clientx.setConnectionTimeout(connectionTimeout);
		clientx.setSoTimeout(soTimeout);
	}

	public JedisX(final JedisShardInfo shardInfo) {
		clientx = new ClientX(shardInfo.getHost(), shardInfo.getPort(), shardInfo.getSsl(),
				shardInfo.getSslSocketFactory(), shardInfo.getSslParameters(), shardInfo.getHostnameVerifier());
		clientx.setConnectionTimeout(shardInfo.getConnectionTimeout());
		clientx.setSoTimeout(shardInfo.getSoTimeout());
		clientx.setPassword(shardInfo.getPassword());
	}

	public JedisX(URI uri) {
		initializeClientFromURI(uri);
	}

	public JedisX(URI uri, final SSLSocketFactory sslSocketFactory, final SSLParameters sslParameters,
			final HostnameVerifier hostnameVerifier) {
		initializeClientFromURI(uri, sslSocketFactory, sslParameters, hostnameVerifier);
	}

	public JedisX(final URI uri, final int timeout) {
		initializeClientFromURI(uri);
		clientx.setConnectionTimeout(timeout);
		clientx.setSoTimeout(timeout);
	}

	public JedisX(final URI uri, final int timeout, final SSLSocketFactory sslSocketFactory,
			final SSLParameters sslParameters, final HostnameVerifier hostnameVerifier) {
		initializeClientFromURI(uri, sslSocketFactory, sslParameters, hostnameVerifier);
		clientx.setConnectionTimeout(timeout);
		clientx.setSoTimeout(timeout);
	}

	public JedisX(final URI uri, final int connectionTimeout, final int soTimeout) {
		initializeClientFromURI(uri);
		clientx.setConnectionTimeout(connectionTimeout);
		clientx.setSoTimeout(soTimeout);
	}

	public JedisX(final URI uri, final int connectionTimeout, final int soTimeout,
			final SSLSocketFactory sslSocketFactory, final SSLParameters sslParameters,
			final HostnameVerifier hostnameVerifier) {
		initializeClientFromURI(uri, sslSocketFactory, sslParameters, hostnameVerifier);
		clientx.setConnectionTimeout(connectionTimeout);
		clientx.setSoTimeout(soTimeout);
	}

	private void initializeClientFromURI(URI uri) {
		if (!JedisURIHelper.isValid(uri)) {
			throw new InvalidURIException(
					String.format("Cannot open Redis connection due invalid URI. %s", uri.toString()));
		}

		clientx = new ClientX(uri.getHost(), uri.getPort(), uri.getScheme().equals("rediss"));

		String password = JedisURIHelper.getPassword(uri);
		if (password != null) {
			clientx.auth(password);
			clientx.getStatusCodeReply();
		}

	}

	private void initializeClientFromURI(URI uri, final SSLSocketFactory sslSocketFactory,
			final SSLParameters sslParameters, final HostnameVerifier hostnameVerifier) {
		if (!JedisURIHelper.isValid(uri)) {
			throw new InvalidURIException(
					String.format("Cannot open Redis connection due invalid URI. %s", uri.toString()));
		}

		clientx = new ClientX(uri.getHost(), uri.getPort(), uri.getScheme().equals("rediss"), sslSocketFactory,
				sslParameters, hostnameVerifier);

		String password = JedisURIHelper.getPassword(uri);
		if (password != null) {
			clientx.auth(password);
			clientx.getStatusCodeReply();
		}

	}

	public String auth(final String password) {
		clientx.auth(password);
		return clientx.getStatusCodeReply();
	}

	public List<String> kpush(final String key, final String value) {
		clientx.kpush(key, value);
		clientx.setTimeoutInfinite();

		try {
			return clientx.getMultiBulkReply();
		} finally {
			clientx.rollbackTimeout();
		}
	}
	
	public List<String> kpush(final String key, final String value, int partition) {
		clientx.kpush(key, value, String.valueOf(partition));
		clientx.setTimeoutInfinite();
		
		try {
			return clientx.getMultiBulkReply();
		} finally {
			clientx.rollbackTimeout();
		}
	}

	public List<String> kpop(final String key) {
		clientx.kpop(key);
		clientx.setTimeoutInfinite();
		try {
			return clientx.getMultiBulkReply();
		} finally {
			clientx.rollbackTimeout();
		}
	}

	public List<String> kpop(final String key, int partition, int offset) {
		clientx.kpop(key, partition, offset);
		clientx.setTimeoutInfinite();
		try {
			return clientx.getMultiBulkReply();
		} finally {
			clientx.rollbackTimeout();
		}
	}

	public List<String> kpop(final String key, int partition, int offset, int max) {
		clientx.kpop(key, partition, offset, max);
		clientx.setTimeoutInfinite();
		try {
			return clientx.getMultiBulkReply();
		} finally {
			clientx.rollbackTimeout();
		}
	}

	public List<String> kpartitions(final String key) {
		clientx.kpartitions(key);
		clientx.setTimeoutInfinite();

		try {
			return clientx.getMultiBulkReply();
		} finally {
			clientx.rollbackTimeout();
		}
	}

	public List<String> koffset(final String key, final String partition, final String timestamps) {
		clientx.koffset(key, partition, timestamps);
		clientx.setTimeoutInfinite();

		try {
			return clientx.getMultiBulkReply();
		} finally {
			clientx.rollbackTimeout();
		}
	}

	@SuppressWarnings("deprecation")
	public void close() {
		if (dataSource != null) {
			if (clientx.isBroken()) {
				this.dataSource.returnBrokenResource(this);
			} else {
				this.dataSource.returnResource(this);
			}
		} else {
			clientx.close();
		}
	}

	public void setDataSource(Pool<JedisX> jedisPool) {
		this.dataSource = jedisPool;
	}
	
	public boolean isConnected() {
		return clientx.isConnected();
	}
	
	public void connect() {
		clientx.connect();
	}
	
	public String quit() {
		clientx.quit();
		return clientx.getStatusCodeReply();
	}
	
	public ClientX getClient() {
		return clientx;
	}
	
	public String ping() {
		clientx.ping();
		return clientx.getStatusCodeReply();
	}
}
