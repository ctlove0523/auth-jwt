package io.github.ctlove0523.auth.jwt.core;

public interface IdentityVerifier {

	boolean validIdentity(Identity identity);
}
