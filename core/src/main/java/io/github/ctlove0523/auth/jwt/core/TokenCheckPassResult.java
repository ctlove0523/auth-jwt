package io.github.ctlove0523.auth.jwt.core;

import java.util.Date;

public class TokenCheckPassResult implements TokenCheckResult {
	private Date expiration;

	public TokenCheckPassResult(Date expiration) {
		this.expiration = expiration;
	}

	@Override
	public boolean pass() {
		return true;
	}

	@Override
	public Exception cause() {
		return null;
	}

	@Override
	public Date getExpiration() {
		return new Date(expiration.getTime());
	}
}
