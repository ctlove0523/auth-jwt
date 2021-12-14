package io.github.ctlove0523.auth.jwt.core;

public class TokenCheckFailResult implements TokenCheckResult {
	private Exception cause;

	TokenCheckFailResult(Exception cause) {
		this.cause = cause;
	}

	@Override
	public boolean pass() {
		return false;
	}

	@Override
	public Exception cause() {
		return cause;
	}
}
