package io.github.ctlove0523.auth.jwt.consul;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.kv.model.GetValue;
import io.github.ctlove0523.auth.jwt.core.ConfigureKey;
import io.github.ctlove0523.auth.jwt.core.NoopTokenCipher;
import io.github.ctlove0523.auth.jwt.core.SignKeyChangeEvent;
import io.github.ctlove0523.auth.jwt.core.SignKeyChangeHandler;
import io.github.ctlove0523.auth.jwt.core.SignKeyProvider;
import io.github.ctlove0523.auth.jwt.core.TokenCipher;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 基于Consul实现使用公私钥生成喝校验token，使用私钥签名，公钥校验
 * https://www.javamex.com/tutorials/cryptography/rsa_encryption.shtml
 */
public class ConsulPubPrivateSignKeyProvider implements SignKeyProvider {
    private final ConsulClient consulClient;
    private final String publicKeyPrefix;
    private Map<String, ConfigureKey> publicKeyMap;
    private final String privateKeyPrefix;
    private Map<String, ConfigureKey> privateKeyMap;
    private final List<SignKeyChangeHandler> handlers = new CopyOnWriteArrayList<>();
    private TokenCipher tokenCipher;

    public ConsulPubPrivateSignKeyProvider(ConsulClient consulClient) {
        this(consulClient, "public.keys", "private.keys", new NoopTokenCipher());
    }

    public ConsulPubPrivateSignKeyProvider(ConsulClient consulClient, String publicKeyPrefix, String privateKeyPrefix,
                                           TokenCipher tokenCipher) {

        Objects.requireNonNull(consulClient, "consulClient");
        Objects.requireNonNull(publicKeyPrefix, "publicKeys");
        Objects.requireNonNull(privateKeyPrefix, "privateKeys");
        Objects.requireNonNull(tokenCipher, "tokenCipher");

        this.tokenCipher = tokenCipher;
        this.consulClient = consulClient;
        this.publicKeyPrefix = publicKeyPrefix;
        this.publicKeyMap = getKeys(this.publicKeyPrefix);
        this.privateKeyPrefix = privateKeyPrefix;
        this.privateKeyMap = getKeys(this.privateKeyPrefix);

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(this::watch, 10L, 1L, TimeUnit.SECONDS);

    }

    @Override
    public Key getSignKey(String identity) {
        return getPrivateKey(privateKeyPrefix, identity);
    }

    @Override
    public Key getVerifyKey(String identity) {
        return getPublicKey(publicKeyPrefix, identity);
    }

    private Key getPrivateKey(String configKey, String identity) {
        ConfigureKey secretKey = privateKeyMap.get(configKey + "." + identity);
        byte[] keyBytes = Base64.getDecoder().decode(secretKey.getKey());

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(secretKey.getAlgorithm());
            return keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private Key getPublicKey(String configKey, String identity) {
        ConfigureKey secretKey = publicKeyMap.get(configKey + "." + identity);
        byte[] keyBytes = Base64.getDecoder().decode(secretKey.getKey());

        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(secretKey.getAlgorithm());
            return keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private Map<String, ConfigureKey> getKeys(String configKeyPrefix) {
        List<GetValue> values = consulClient.getKVValues(configKeyPrefix).getValue();
        Map<String, ConfigureKey> configureKeyMap = new HashMap<>(values.size());
        for (GetValue getValue : values) {
            String configKeyString = getValue.getDecodedValue(StandardCharsets.UTF_8);
            ConfigureKey configureKey = new ConfigureKey(configKeyString, tokenCipher);
            configureKeyMap.put(getValue.getKey(), configureKey);
        }

        return configureKeyMap;
    }

    @Override
    public void registerHandler(SignKeyChangeHandler handler) {
        Objects.requireNonNull(handler, "handler");
        handlers.add(handler);
    }

    private void watch() {
        Map<String, ConfigureKey> newPublicKeyMap = getKeys(this.publicKeyPrefix);
        Map<String, ConfigureKey> newPrivateKeyMap = getKeys(this.privateKeyPrefix);

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

    private List<String> changedKeys(Map<String, ConfigureKey> oldKeys, Map<String, ConfigureKey> newKeys) {
        List<String> keys = new ArrayList<>();
        for (Map.Entry<String, ConfigureKey> entry : oldKeys.entrySet()) {
            String key = entry.getKey();
            if (!newKeys.containsKey(key)) {
                keys.add(key);
                continue;
            }
            ConfigureKey value = entry.getValue();
            if (!newKeys.get(key).equals(value)) {
                keys.add(key);
            }
        }

        return keys;
    }
}
