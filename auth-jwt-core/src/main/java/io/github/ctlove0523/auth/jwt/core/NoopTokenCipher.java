package io.github.ctlove0523.auth.jwt.core;

class NoopTokenCipher implements TokenCipher {
    @Override
    public String encrypt(String plainToken) {
        return plainToken;
    }

    @Override
    public String decrypt(String cipherToken) {
        return cipherToken;
    }

    @Override
    public String hashCode(String plainToken) {
        return plainToken;
    }
}
