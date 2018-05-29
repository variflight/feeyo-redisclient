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

public class FeeyoJedis {
	protected FeeyoClient client = null;
	protected Pool<FeeyoJedis> dataSource = null;

	public FeeyoJedis() {
		client = new FeeyoClient();
	}

	public FeeyoJedis(final String host) {
		URI uri = URI.create(host);
		if (uri.getScheme() != null && (uri.getScheme().equals("redis") || uri.getScheme().equals("rediss"))) {
			initializeClientFromURI(uri);
		} else {
			client = new FeeyoClient(host);
		}
	}

	public FeeyoJedis(final String host, final int port) {
		client = new FeeyoClient(host, port);
	}

	public FeeyoJedis(final String host, final int port, final boolean ssl) {
		client = new FeeyoClient(host, port, ssl);
	}

	public FeeyoJedis(final String host, final int port, final boolean ssl, final SSLSocketFactory sslSocketFactory,
			final SSLParameters sslParameters, final HostnameVerifier hostnameVerifier) {
		client = new FeeyoClient(host, port, ssl, sslSocketFactory, sslParameters, hostnameVerifier);
	}

	public FeeyoJedis(final String host, final int port, final int timeout) {
		client = new FeeyoClient(host, port);
		client.setConnectionTimeout(timeout);
		client.setSoTimeout(timeout);
	}

	public FeeyoJedis(final String host, final int port, final int timeout, final boolean ssl) {
		client = new FeeyoClient(host, port, ssl);
		client.setConnectionTimeout(timeout);
		client.setSoTimeout(timeout);
	}

	public FeeyoJedis(final String host, final int port, final int timeout, final boolean ssl,
			final SSLSocketFactory sslSocketFactory, final SSLParameters sslParameters,
			final HostnameVerifier hostnameVerifier) {
		client = new FeeyoClient(host, port, ssl, sslSocketFactory, sslParameters, hostnameVerifier);
		client.setConnectionTimeout(timeout);
		client.setSoTimeout(timeout);
	}

	public FeeyoJedis(final String host, final int port, final int connectionTimeout, final int soTimeout) {
		client = new FeeyoClient(host, port);
		client.setConnectionTimeout(connectionTimeout);
		client.setSoTimeout(soTimeout);
	}

	public FeeyoJedis(final String host, final int port, final int connectionTimeout, final int soTimeout,
			final boolean ssl) {
		client = new FeeyoClient(host, port, ssl);
		client.setConnectionTimeout(connectionTimeout);
		client.setSoTimeout(soTimeout);
	}

	public FeeyoJedis(final String host, final int port, final int connectionTimeout, final int soTimeout,
			final boolean ssl, final SSLSocketFactory sslSocketFactory, final SSLParameters sslParameters,
			final HostnameVerifier hostnameVerifier) {
		client = new FeeyoClient(host, port, ssl, sslSocketFactory, sslParameters, hostnameVerifier);
		client.setConnectionTimeout(connectionTimeout);
		client.setSoTimeout(soTimeout);
	}

	public FeeyoJedis(final JedisShardInfo shardInfo) {
		client = new FeeyoClient(shardInfo.getHost(), shardInfo.getPort(), shardInfo.getSsl(),
				shardInfo.getSslSocketFactory(), shardInfo.getSslParameters(), shardInfo.getHostnameVerifier());
		client.setConnectionTimeout(shardInfo.getConnectionTimeout());
		client.setSoTimeout(shardInfo.getSoTimeout());
		client.setPassword(shardInfo.getPassword());
	}

	public FeeyoJedis(URI uri) {
		initializeClientFromURI(uri);
	}

	public FeeyoJedis(URI uri, final SSLSocketFactory sslSocketFactory, final SSLParameters sslParameters,
			final HostnameVerifier hostnameVerifier) {
		initializeClientFromURI(uri, sslSocketFactory, sslParameters, hostnameVerifier);
	}

	public FeeyoJedis(final URI uri, final int timeout) {
		initializeClientFromURI(uri);
		client.setConnectionTimeout(timeout);
		client.setSoTimeout(timeout);
	}

	public FeeyoJedis(final URI uri, final int timeout, final SSLSocketFactory sslSocketFactory,
			final SSLParameters sslParameters, final HostnameVerifier hostnameVerifier) {
		initializeClientFromURI(uri, sslSocketFactory, sslParameters, hostnameVerifier);
		client.setConnectionTimeout(timeout);
		client.setSoTimeout(timeout);
	}

	public FeeyoJedis(final URI uri, final int connectionTimeout, final int soTimeout) {
		initializeClientFromURI(uri);
		client.setConnectionTimeout(connectionTimeout);
		client.setSoTimeout(soTimeout);
	}

	public FeeyoJedis(final URI uri, final int connectionTimeout, final int soTimeout,
			final SSLSocketFactory sslSocketFactory, final SSLParameters sslParameters,
			final HostnameVerifier hostnameVerifier) {
		initializeClientFromURI(uri, sslSocketFactory, sslParameters, hostnameVerifier);
		client.setConnectionTimeout(connectionTimeout);
		client.setSoTimeout(soTimeout);
	}

	private void initializeClientFromURI(URI uri) {
		if (!JedisURIHelper.isValid(uri)) {
			throw new InvalidURIException(
					String.format("Cannot open Redis connection due invalid URI. %s", uri.toString()));
		}

		client = new FeeyoClient(uri.getHost(), uri.getPort(), uri.getScheme().equals("rediss"));

		String password = JedisURIHelper.getPassword(uri);
		if (password != null) {
			client.auth(password);
			client.getStatusCodeReply();
		}

	}

	private void initializeClientFromURI(URI uri, final SSLSocketFactory sslSocketFactory,
			final SSLParameters sslParameters, final HostnameVerifier hostnameVerifier) {
		if (!JedisURIHelper.isValid(uri)) {
			throw new InvalidURIException(
					String.format("Cannot open Redis connection due invalid URI. %s", uri.toString()));
		}

		client = new FeeyoClient(uri.getHost(), uri.getPort(), uri.getScheme().equals("rediss"), sslSocketFactory,
				sslParameters, hostnameVerifier);

		String password = JedisURIHelper.getPassword(uri);
		if (password != null) {
			client.auth(password);
			client.getStatusCodeReply();
		}

	}

	public String auth(final String password) {
		client.auth(password);
		return client.getStatusCodeReply();
	}

	public List<String> kpush(final String key, final String value) {
		client.kpush(key, value);
		client.setTimeoutInfinite();

		try {
			return client.getMultiBulkReply();
		} finally {
			client.rollbackTimeout();
		}
	}
	
	public List<String> kpush(final String key, final String value, int partition) {
		client.kpush(key, value, String.valueOf(partition));
		client.setTimeoutInfinite();
		
		try {
			return client.getMultiBulkReply();
		} finally {
			client.rollbackTimeout();
		}
	}

	public List<String> kpop(final String key) {
		client.kpop(key);
		client.setTimeoutInfinite();
		try {
			return client.getMultiBulkReply();
		} finally {
			client.rollbackTimeout();
		}
	}

	public List<String> kpop(final String key, int partition, int offset) {
		client.kpop(key, partition, offset);
		client.setTimeoutInfinite();
		try {
			return client.getMultiBulkReply();
		} finally {
			client.rollbackTimeout();
		}
	}

	public List<String> kpop(final String key, int partition, int offset, int max) {
		client.kpop(key, partition, offset, max);
		client.setTimeoutInfinite();
		try {
			return client.getMultiBulkReply();
		} finally {
			client.rollbackTimeout();
		}
	}

	public List<String> kpartitions(final String key) {
		client.kpartitions(key);
		client.setTimeoutInfinite();

		try {
			return client.getMultiBulkReply();
		} finally {
			client.rollbackTimeout();
		}
	}

	public List<String> koffset(final String key, final String partition, final String timestamps) {
		client.koffset(key, partition, timestamps);
		client.setTimeoutInfinite();

		try {
			return client.getMultiBulkReply();
		} finally {
			client.rollbackTimeout();
		}
	}

	public void close() {
		if (dataSource != null) {
			if (client.isBroken()) {
				this.dataSource.returnBrokenResource(this);
			} else {
				this.dataSource.returnResource(this);
			}
		} else {
			client.close();
		}
	}

	public void setDataSource(Pool<FeeyoJedis> jedisPool) {
		this.dataSource = jedisPool;
	}
	
	public boolean isConnected() {
		return client.isConnected();
	}
	
	public void connect() {
		client.connect();
	}
	
	public String quit() {
		client.quit();
		return client.getStatusCodeReply();
	}
	
	public FeeyoClient getClient() {
		return client;
	}
	
	public String ping() {
		client.ping();
		return client.getStatusCodeReply();
	}

 
	public static void main(String[] args) {
		FeeyoJedis jedis = new FeeyoJedis("127.0.0.1", 8066);
		jedis.auth("tod_yt_kfktest_fdafd4809kimchgdfh");
		System.out.println(jedis.kpush("test01", "222"));
		System.out.println(jedis.kpop("test01"));
		System.out.println(jedis.kpop("test01", 1, 0));
		System.out.println(jedis.kpartitions("test01"));
		System.out.println(jedis.koffset("test01", "0", "-1"));
	}

}
