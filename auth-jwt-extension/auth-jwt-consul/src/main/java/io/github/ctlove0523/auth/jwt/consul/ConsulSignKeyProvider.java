package io.github.ctlove0523.auth.jwt.consul;

import com.ecwid.consul.v1.ConsulClient;
import io.github.ctlove0523.auth.jwt.core.Identity;
import io.github.ctlove0523.auth.jwt.core.JacksonUtil;
import io.github.ctlove0523.auth.jwt.core.SignKeyChangeHandler;
import io.github.ctlove0523.auth.jwt.core.SignKeyProvider;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ConsulSignKeyProvider implements SignKeyProvider {
    private final ConsulClient consulClient;
    private final String configKey;
    private final String configsKey;
    private List<SignKeyChangeHandler> handlers = new CopyOnWriteArrayList<>();
    private Optional<String> val = Optional.empty();

    public ConsulSignKeyProvider(ConsulClient consulClient) {
        this(consulClient, "jwt.key", "jwt.keys");
    }

    public ConsulSignKeyProvider(ConsulClient consulClient, String configKey) {
        this(consulClient, configKey, "jwt.keys");
    }


    public ConsulSignKeyProvider(ConsulClient consulClient, String configKey, String configsKey) {
        Objects.requireNonNull(consulClient, "consulClient");
        Objects.requireNonNull(configKey, "configKey");
        Objects.requireNonNull(configsKey, "configsKey");
        this.consulClient = consulClient;
        this.configKey = configKey;
        this.configsKey = configsKey;
        this.val = Optional.ofNullable(getSignKey());
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(this::watch, 10L, 1L, TimeUnit.SECONDS);

    }

    @Override
    public String getSignKey() {
        return val.orElseGet(() -> consulClient.getKVValue(configKey).getValue().getDecodedValue());
    }

    @Override
    public String getSignKey(String identity) {
        String configSignKeys = consulClient.getKVValue(configsKey).getValue().getDecodedValue(StandardCharsets.UTF_8);
        Map<String, String> keys = (Map<String, String>) JacksonUtil.json2Pojo(configSignKeys, Map.class);
        if (Objects.isNull(keys)) {
            return "";
        }
        return keys.get(identity);
    }

    @Override
    public void registerHandler(SignKeyChangeHandler handler) {
        Objects.requireNonNull(handler, "handler");
        handlers.add(handler);
    }

    private void watch() {
        String newValue = consulClient.getKVValue(configKey).getValue().getDecodedValue();
        String oldValue = val.orElse("");
        if (newValue != null && !newValue.equals(val.orElse(""))) {
            val = Optional.of(newValue);
            handlers.forEach(handler -> handler.handle(oldValue, newValue));
        }

    }
}
