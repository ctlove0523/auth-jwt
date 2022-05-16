package io.github.ctlove0523.auth.jwt.core;

public interface TokenCipher {

    String encrypt(String plainToken);

    String decrypt(String cipherToken);

    String hashCode(String plainToken);
}
