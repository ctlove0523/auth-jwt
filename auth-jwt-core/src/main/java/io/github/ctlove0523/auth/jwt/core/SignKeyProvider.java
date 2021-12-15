package io.github.ctlove0523.auth.jwt.core;

public interface SignKeyProvider {

	String getSignKey();

	void registerHandler(SignKeyChangeHandler handler);
}
