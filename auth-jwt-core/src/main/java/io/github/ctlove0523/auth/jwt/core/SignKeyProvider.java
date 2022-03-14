package io.github.ctlove0523.auth.jwt.core;

import java.security.Key;

/**
 * 负责token签名和验证签名的Key
 */
public interface SignKeyProvider {

    /**
     * 获取某个身份对应的签名token key
     *
     * @param identity 身份信息
     * @return {@link Key}
     */
    Key getSignKey(String identity);

    /**
     * 获取某个身份对应的校验token签名的key
     *
     * @param identity 身份信息
     * @return {@link Key}
     */
    Key getVerifyKey(String identity);

    /**
     * 注册key变化回调函数
     *
     * @param handler {@link SignKeyChangeHandler}
     */
    void registerHandler(SignKeyChangeHandler handler);
}
