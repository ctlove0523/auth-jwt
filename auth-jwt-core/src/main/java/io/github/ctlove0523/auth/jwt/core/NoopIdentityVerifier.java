package io.github.ctlove0523.auth.jwt.core;

/**
 * do no verify just return true
 */
public class NoopIdentityVerifier implements IdentityVerifier {
    @Override
    public boolean validIdentity(Identity identity) {
        return true;
    }
}
