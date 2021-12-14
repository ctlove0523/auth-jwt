package io.github.ctlove0523.auth.jwt.core;

import java.util.Date;

public interface TokenCheckResult {

	boolean pass();

	Exception cause();

	default Date getExpiration() {
		return new Date();
	}
}
