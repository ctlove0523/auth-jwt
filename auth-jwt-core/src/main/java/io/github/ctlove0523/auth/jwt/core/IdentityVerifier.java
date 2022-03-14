package io.github.ctlove0523.auth.jwt.core;

/**
 * 身份校验器
 */
public interface IdentityVerifier {

    /**
     * 校验身份是否合法
     *
     * @param identity 身份信息
     * @return 合法返回true，否则返回false
     */
    boolean validIdentity(Identity identity);
}
