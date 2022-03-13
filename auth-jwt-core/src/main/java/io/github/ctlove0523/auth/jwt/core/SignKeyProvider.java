package io.github.ctlove0523.auth.jwt.core;

import java.security.Key;

public interface SignKeyProvider {


	Key getSignKey(String identity);

	Key getVerifyKey(String identity);

	SignKeyType getType();

	void registerHandler(SignKeyChangeHandler handler);
}
