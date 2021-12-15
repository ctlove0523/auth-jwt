package io.github.ctlove0523.auth.jwt.core;

public interface TokenClient {

	String getToken(Identity identity);

	TokenCheckResult validToken(String token);
}
