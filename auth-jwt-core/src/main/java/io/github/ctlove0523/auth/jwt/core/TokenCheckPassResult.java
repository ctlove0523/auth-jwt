package io.github.ctlove0523.auth.jwt.core;

import java.util.Date;

public class TokenCheckPassResult implements TokenCheckResult {
	private Date expiration;
	private Identity identity;

	public TokenCheckPassResult(Date expiration,Identity identity) {
		this.expiration = expiration;
		this.identity = identity;
	}

	@Override
	public boolean isPass() {
		return true;
	}

	@Override
	public Identity getIdentity() {
		return identity;
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
