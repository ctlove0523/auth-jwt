package io.github.ctlove0523.auth.jwt.core;

/**
 * 定义key配置的格式
 */
public class ConfigureKey {
    /**
     * Key生成算法,
     */
    private final String algorithm;

    /**
     * 经过base64编码后的值，明文
     */
    private final String key;

    public ConfigureKey(String input, TokenCipher tokenCipher) {
        String[] algAndKey = input.split(":");
        this.algorithm = algAndKey[0];
        this.key = tokenCipher.decrypt(algAndKey[1]);
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public String getKey() {
        return key;
    }
}
