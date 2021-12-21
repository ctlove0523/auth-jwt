package io.github.ctlove0523.auth.jwt.core;

public class TokenCheckFailResult implements TokenCheckResult {
	private Exception cause;

	TokenCheckFailResult(Exception cause) {
		this.cause = cause;
	}

	@Override
	public boolean isPass() {
		return false;
	}

	@Override
	public Identity getIdentity() {
		return null;
	}

	@Override
	public Exception cause() {
		return cause;
	}
}
