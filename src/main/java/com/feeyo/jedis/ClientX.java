package com.feeyo.jedis;

import static com.feeyo.jedis.ProtocolX.Command.AUTH;
import static com.feeyo.jedis.ProtocolX.Command.KPOP;
import static com.feeyo.jedis.ProtocolX.Command.QUIT;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocketFactory;

import com.feeyo.jedis.ProtocolX.Command;

import redis.clients.util.SafeEncoder;


public class ClientX extends ConnectionX {
	
	public enum LIST_POSITION {
		BEFORE, AFTER;
		public final byte[] raw;

		private LIST_POSITION() {
			raw = SafeEncoder.encode(name());
		}
	}

	private String password;

	public ClientX() {
		super();
	}

	public ClientX(final String host) {
		super(host);
	}

	public ClientX(final String host, final int port) {
		super(host, port);
	}

	public ClientX(final String host, final int port, final boolean ssl) {
		super(host, port, ssl);
	}

	public ClientX(final String host, final int port, final boolean ssl, final SSLSocketFactory sslSocketFactory,
			final SSLParameters sslParameters, final HostnameVerifier hostnameVerifier) {
		super(host, port, ssl, sslSocketFactory, sslParameters, hostnameVerifier);
	}

	private byte[][] joinParameters(byte[] first, byte[]... rest) {
		byte[][] result = new byte[rest.length + 1][];
		result[0] = first;
		System.arraycopy(rest, 0, result, 1, rest.length);
		return result;
	}

	public void setPassword(final String password) {
		this.password = password;
	}

	@Override
	public void connect() {
		if (!isConnected()) {
			super.connect();
			if (password != null) {
				auth(password);
				getStatusCodeReply();
			}
		}
	}

	public void auth(final String password) {
		setPassword(password);
		sendCommand(AUTH, password);
	}
	
	public void quit() {
		sendCommand(QUIT);
	}
	
	public void ping() {
		sendCommand(Command.PING);
	}

	public void kpop(final String key) {
		sendCommand(KPOP, SafeEncoder.encode(key));
	}

	public void kpop(final String key, int partition, int offset) {
		sendCommand(KPOP, joinParameters(SafeEncoder.encode(key),
				SafeEncoder.encodeMany(String.valueOf(partition), String.valueOf(offset))));
	}

	public void kpop(final String key, int partition, int offset, int max) {
		sendCommand(KPOP, joinParameters(SafeEncoder.encode(key),
				SafeEncoder.encodeMany(String.valueOf(partition), String.valueOf(offset), String.valueOf(max))));
	}

	public void kpush(final String key, final String... value) {
		sendCommand(Command.KPUSH, joinParameters(SafeEncoder.encode(key), SafeEncoder.encodeMany(value)));
	}

	public void kpartitions(final String key) {
		sendCommand(Command.KPARTITIONS, SafeEncoder.encode(key));
	}

	public void koffset(final String key, final String partition, final String timestamps) {
		sendCommand(Command.KOFFSET,
				joinParameters(SafeEncoder.encode(key), SafeEncoder.encodeMany(partition, timestamps)));
	}
}
