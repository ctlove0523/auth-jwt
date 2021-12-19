package io.github.ctlove0523.auth.jwt.consul;

import com.ecwid.consul.v1.ConsulClient;
import io.github.ctlove0523.auth.jwt.core.SignKeyChangeHandler;
import io.github.ctlove0523.auth.jwt.core.SignKeyProvider;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ConsulSignKeyProvider implements SignKeyProvider {
    private final ConsulClient consulClient;
    private final String configKey;
    private List<SignKeyChangeHandler> handlers = new CopyOnWriteArrayList<>();
    private Optional<String> val = Optional.empty();

    public ConsulSignKeyProvider(ConsulClient consulClient) {
        this(consulClient, "jwt.key");
    }

    public ConsulSignKeyProvider(ConsulClient consulClient, String configKey) {
        Objects.requireNonNull(consulClient, "consulClient");
        Objects.requireNonNull(configKey, "configKey");
        this.consulClient = consulClient;
        this.configKey = configKey;
        this.val = Optional.ofNullable(getSignKey());
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(this::watch, 10L, 1L, TimeUnit.SECONDS);
    }

    @Override
    public String getSignKey() {
        return val.orElseGet(() -> consulClient.getKVValue(configKey).getValue().getDecodedValue());
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
