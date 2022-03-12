package io.github.ctlove0523.auth.jwt.core;

public interface SignKeyProvider {

	String getSignKey();

	default String getSignKey(String  identity) {
		return "";
	}

	void registerHandler(SignKeyChangeHandler handler);
}
