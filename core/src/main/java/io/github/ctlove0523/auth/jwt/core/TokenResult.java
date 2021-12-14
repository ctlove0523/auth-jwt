package io.github.ctlove0523.auth.jwt.core;

public interface TokenResult {
    /**
     * 字符串格式的token
     * @return token
     */
    String getToken();

    /**
     * token自创建后多久过期
     * @return 毫秒为单位的过期事件
     */
    long expireAfterCreate();
}
