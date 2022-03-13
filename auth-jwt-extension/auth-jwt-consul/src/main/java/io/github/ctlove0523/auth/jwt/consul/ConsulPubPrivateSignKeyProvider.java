package io.github.ctlove0523.auth.jwt.consul;

import com.ecwid.consul.v1.ConsulClient;
import io.github.ctlove0523.auth.jwt.core.JacksonUtil;
import io.github.ctlove0523.auth.jwt.core.SignKeyChangeHandler;
import io.github.ctlove0523.auth.jwt.core.SignKeyProvider;
import io.github.ctlove0523.auth.jwt.core.SignKeyType;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 基于Consul实现使用公私钥生成喝校验token，使用私钥签名，公钥校验
 */
public class ConsulPubPrivateSignKeyProvider implements SignKeyProvider {
    private final ConsulClient consulClient;
    private final String publicKeys;
    private final String privateKeys;
    private List<SignKeyChangeHandler> handlers = new CopyOnWriteArrayList<>();

    public ConsulPubPrivateSignKeyProvider(ConsulClient consulClient) {
        this(consulClient, "public.keys", "private.keys");
    }

    public ConsulPubPrivateSignKeyProvider(ConsulClient consulClient, String publicKeys, String privateKeys) {
        Objects.requireNonNull(consulClient, "consulClient");
        Objects.requireNonNull(publicKeys, "publicKeys");
        Objects.requireNonNull(privateKeys, "privateKeys");
        this.consulClient = consulClient;
        this.publicKeys = publicKeys;
        this.privateKeys = privateKeys;
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(this::watch, 10L, 1L, TimeUnit.SECONDS);

    }


    @Override
    public Key getSignKey(String identity) {
        return getPrivateKey(privateKeys, identity);
    }

    @Override
    public Key getVerifyKey(String identity) {
        return getPublicKey(publicKeys, identity);
    }

    private String getKey(String configKey, String identity) {
        String configSignKeys = consulClient.getKVValue(configKey).getValue().getDecodedValue(StandardCharsets.UTF_8);
        Map<String, String> keys = (Map<String, String>) JacksonUtil.json2Pojo(configSignKeys, Map.class);
        if (Objects.isNull(keys)) {
            return null;
        }

        return keys.get(identity);
    }

    private Key getPrivateKey(String configKey, String identity) {
        String secretKey = getKey(configKey, identity);
        byte[] keyBytes = Base64.getDecoder().decode(secretKey.getBytes(StandardCharsets.UTF_8));

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {

        }

        return null;
    }

    private Key getPublicKey(String configKey, String identity) {
        String secretKey = getKey(configKey, identity);
        byte[] keyBytes = Base64.getDecoder().decode(secretKey.getBytes(StandardCharsets.UTF_8));

        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public SignKeyType getType() {
        return SignKeyType.RsaKeyType;
    }

    @Override
    public void registerHandler(SignKeyChangeHandler handler) {
        Objects.requireNonNull(handler, "handler");
        handlers.add(handler);
    }

    private void watch() {

    }
}
