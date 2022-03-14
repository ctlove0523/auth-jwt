package io.github.ctlove0523.auth.jwt.consul;

import com.ecwid.consul.v1.ConsulClient;
import io.github.ctlove0523.auth.jwt.core.JacksonUtil;
import io.github.ctlove0523.auth.jwt.core.SignKeyChangeEvent;
import io.github.ctlove0523.auth.jwt.core.SignKeyChangeHandler;
import io.github.ctlove0523.auth.jwt.core.SignKeyProvider;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
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
    private Map<String, String> publicKeyMap;
    private final String privateKeys;
    private Map<String, String> privateKeyMap;
    private final List<SignKeyChangeHandler> handlers = new CopyOnWriteArrayList<>();

    public ConsulPubPrivateSignKeyProvider(ConsulClient consulClient) {
        this(consulClient, "public.keys", "private.keys");
    }

    public ConsulPubPrivateSignKeyProvider(ConsulClient consulClient, String publicKeys, String privateKeys) {
        Objects.requireNonNull(consulClient, "consulClient");
        Objects.requireNonNull(publicKeys, "publicKeys");
        Objects.requireNonNull(privateKeys, "privateKeys");

        this.consulClient = consulClient;
        this.publicKeys = publicKeys;
        this.publicKeyMap = getKeys(this.publicKeys);
        this.privateKeys = privateKeys;
        this.privateKeyMap = getKeys(this.privateKeys);

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

    private Map<String, String> getKeys(String configKey) {
        String configSignKeys = consulClient.getKVValue(configKey).getValue().getDecodedValue(StandardCharsets.UTF_8);
        return JacksonUtil.json2Map(configSignKeys);
    }

    private String getKey(String configKey, String identity) {
        Map<String, String> keys = getKeys(configKey);

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
            e.printStackTrace();
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
    public void registerHandler(SignKeyChangeHandler handler) {
        Objects.requireNonNull(handler, "handler");
        handlers.add(handler);
    }

    private void watch() {
        Map<String, String> newPublicKeyMap = getKeys(this.publicKeys);
        Map<String, String> newPrivateKeyMap = getKeys(this.privateKeys);

        List<String> changedPublicKeys = changedKeys(this.publicKeyMap, newPrivateKeyMap);
        this.publicKeyMap = newPublicKeyMap;
        List<String> changedPrivateKeys = changedKeys(this.privateKeyMap, newPrivateKeyMap);
        this.privateKeyMap = newPrivateKeyMap;

        for (SignKeyChangeHandler handler : handlers) {
            for (String key : changedPublicKeys) {
                SignKeyChangeEvent event = new SignKeyChangeEvent();
                event.setOwner(key);
                handler.handle(event);
            }
        }

        for (SignKeyChangeHandler handler : handlers) {
            for (String key : changedPrivateKeys) {
                SignKeyChangeEvent event = new SignKeyChangeEvent();
                event.setOwner(key);
                handler.handle(event);
            }
        }
    }

    private List<String> changedKeys(Map<String, String> oldKeys, Map<String, String> newKeys) {
        List<String> keys = new ArrayList<>();
        for (Map.Entry<String, String> entry : oldKeys.entrySet()) {
            String key = entry.getKey();
            if (!newKeys.containsKey(key)) {
                keys.add(key);
                continue;
            }
            String value = entry.getValue();
            if (!newKeys.get(key).equals(value)) {
                keys.add(key);
            }
        }

        return keys;
    }
}
